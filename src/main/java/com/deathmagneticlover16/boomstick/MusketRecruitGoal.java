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
    private int reloadTicks;
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

        if (target == null || !target.isAlive()) {
            markIdle(musket);
            actionTicks = 0;
            return;
        }

        recruit.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distanceSquared = recruit.distanceToSqr(target);

        if (!MusketItem.isLoaded(musket)) {
            if (distanceSquared <= 4.0D) {
                markActive(musket);
                reloadTicks = 0;
                if (bayonetCooldown == 0) {
                    target.hurt(recruit.damageSources().mobAttack(recruit), 6.0F);
                    bayonetCooldown = 20;
                }
            } else if (++reloadTicks >= reloadLength(musket)) {
                markActive(musket);
                MusketItem.setLoaded(musket, true);
                musket.getOrCreateTag().remove("RecruitReloadTicks");
                reloadTicks = 0;
            } else markActive(musket);
            return;
        }

        reloadTicks = 0;
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

    private int reloadLength(ItemStack musket) {
        if (!musket.getOrCreateTag().contains("RecruitReloadTicks")) {
            musket.getOrCreateTag().putInt("RecruitReloadTicks", 300 + recruit.getRandom().nextInt(101));
        }
        return musket.getOrCreateTag().getInt("RecruitReloadTicks");
    }
}
