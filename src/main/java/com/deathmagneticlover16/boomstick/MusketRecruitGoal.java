package com.deathmagneticlover16.boomstick;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

final class MusketRecruitGoal extends Goal {
    private final Mob recruit;
    private int actionTicks;
    private int idleTicks;
    private int bayonetCooldown;

    MusketRecruitGoal(Mob recruit) {
        this.recruit = recruit;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return recruit.getMainHandItem().is(BoomstickMod.MUSKET.get());
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        ItemStack musket = recruit.getMainHandItem();
        LivingEntity target = recruit.getTarget();
        if (bayonetCooldown > 0) bayonetCooldown--;
        boolean hasTarget = target != null && target.isAlive();
        double distanceSquared = hasTarget ? recruit.distanceToSqr(target) : Double.MAX_VALUE;

        if (!MusketItem.isLoaded(musket)) {
            if (hasTarget && distanceSquared <= 4.0D) {
                markActive(musket);
                musket.getOrCreateTag().remove("RecruitReloadAt");
                if (bayonetCooldown == 0) {
                    target.hurt(recruit.damageSources().mobAttack(recruit), 6.0F);
                    bayonetCooldown = 20;
                }
            } else if (recruit.level().getGameTime() >= reloadDeadline(musket)) {
                markActive(musket);
                MusketItem.setLoaded(musket, true);
                musket.getOrCreateTag().remove("RecruitReloadAt");
            } else markActive(musket);
            return;
        }

        musket.getOrCreateTag().remove("RecruitReloadAt");
        if (!hasTarget) {
            markIdle(musket);
            actionTicks = 0;
            return;
        }

        recruit.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (distanceSquared <= 2304.0D && recruit.hasLineOfSight(target)) {
            markActive(musket);
            if (actionTicks == 0) {
                actionTicks = 8;
                MusketItem.playReport((ServerLevel) recruit.level(), recruit);
            } else if (--actionTicks == 0) {
                MusketItem.setLoaded(musket, false);
                MusketItem.fire((ServerLevel) recruit.level(), recruit);
            }
        } else {
            actionTicks = 0;
            markIdle(musket);
        }
    }

    private void markActive(ItemStack musket) {
        idleTicks = 0;
        musket.getOrCreateTag().putBoolean("Shouldered", false);
    }

    private void markIdle(ItemStack musket) {
        if (++idleTicks >= 50) musket.getOrCreateTag().putBoolean("Shouldered", true);
    }

    private long reloadDeadline(ItemStack musket) {
        if (!musket.getOrCreateTag().contains("RecruitReloadAt")) {
            musket.getOrCreateTag().putLong("RecruitReloadAt",
                    recruit.level().getGameTime() + 300L + recruit.getRandom().nextInt(101));
        }
        return musket.getOrCreateTag().getLong("RecruitReloadAt");
    }
}
