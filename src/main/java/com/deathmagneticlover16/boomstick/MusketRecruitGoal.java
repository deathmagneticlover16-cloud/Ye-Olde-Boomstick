package com.deathmagneticlover16.boomstick;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.function.Predicate;

final class MusketRecruitGoal extends Goal {
    private final Mob recruit;
    private int actionTicks;
    private int shotDelayTicks = -1;
    private int idleTicks;
    private int bayonetCooldown;
    private final Method shouldStrategicFireMethod;
    private final Method strategicFirePositionMethod;
    private final Method switchMainHandItemMethod;

    MusketRecruitGoal(Mob recruit) {
        this.recruit = recruit;
        this.shouldStrategicFireMethod = findMethod(recruit.getClass(), "getShouldStrategicFire");
        this.strategicFirePositionMethod = findMethod(recruit.getClass(), "getStrategicFirePos");
        this.switchMainHandItemMethod = findMethod(recruit.getClass(), "switchMainHandItem", Predicate.class);
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (recruit.getMainHandItem().is(BoomstickMod.MUSKET.get())) return true;
        LivingEntity target = recruit.getTarget();
        if ((target != null && target.isAlive()) || strategicAimPosition() != null) switchToMusket();
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
        Vec3 strategicAim = hasTarget ? null : strategicAimPosition();

        if (!MusketItem.isArmed(musket)) {
            if (hasTarget && distanceSquared <= 4.0D) {
                markActive(musket);
                recruit.stopUsingItem();
                cancelReload(musket);
                if (bayonetCooldown == 0) {
                    target.hurt(recruit.damageSources().mobAttack(recruit), 6.0F);
                    bayonetCooldown = 20;
                }
            } else if (recruit.level().getGameTime() >= reloadDeadline(musket)) {
                markActive(musket);
                holdMusketUp();
                MusketItem.setArmed(musket, true);
                musket.getOrCreateTag().remove("RecruitReloadAt");
                BoomstickNetwork.stopReloadSound(recruit);
                MusketItem.playActionSound((ServerLevel) recruit.level(), recruit, BoomstickMod.MUSKET_ARMED.get(), 1.0F);
                recruit.stopUsingItem();
            } else {
                markActive(musket);
                holdMusketUp();
            }
            return;
        }

        cancelReload(musket);
        if (!hasTarget && strategicAim == null) {
            markIdle(musket);
            actionTicks = 0;
            return;
        }

        Vec3 aimPosition = hasTarget ? target.getEyePosition() : strategicAim;
        distanceSquared = recruit.position().distanceToSqr(aimPosition);
        recruit.getLookControl().setLookAt(aimPosition.x, aimPosition.y, aimPosition.z, 30.0F, 30.0F);
        boolean canFire = hasTarget ? distanceSquared <= 2304.0D && recruit.hasLineOfSight(target) : true;
        if (canFire) {
            markActive(musket);
            recruit.stopUsingItem();
            if (actionTicks == 0) {
                if (shotDelayTicks < 0) shotDelayTicks = recruit.getRandom().nextInt(2);
                if (shotDelayTicks-- > 0) return;
                shotDelayTicks = -1;
                actionTicks = 8;
                MusketItem.playReport((ServerLevel) recruit.level(), recruit);
            } else if (--actionTicks == 0) {
                MusketItem.setArmed(musket, false);
                Vec3 direction = aimPosition.subtract(recruit.getEyePosition());
                MusketItem.fire((ServerLevel) recruit.level(), recruit, direction);
            }
        } else {
            actionTicks = 0;
            shotDelayTicks = -1;
            markIdle(musket);
        }
    }

    private void markActive(ItemStack musket) {
        idleTicks = 0;
        musket.getOrCreateTag().putBoolean("Shouldered", false);
    }

    private void markIdle(ItemStack musket) {
        recruit.stopUsingItem();
        if (++idleTicks >= 50) {
            musket.getOrCreateTag().putBoolean("Shouldered", true);
        }
    }

    private long reloadDeadline(ItemStack musket) {
        if (!musket.getOrCreateTag().contains("RecruitReloadAt")) {
            musket.getOrCreateTag().putLong("RecruitReloadAt", recruit.level().getGameTime() + 300L);
            BoomstickNetwork.startReloadSound(recruit);
        }
        return musket.getOrCreateTag().getLong("RecruitReloadAt");
    }

    @Override
    public void stop() {
        cancelReload(recruit.getMainHandItem());
        recruit.stopUsingItem();
        actionTicks = 0;
        shotDelayTicks = -1;
    }

    private void cancelReload(ItemStack musket) {
        if (!musket.getOrCreateTag().contains("RecruitReloadAt")) return;
        musket.getOrCreateTag().remove("RecruitReloadAt");
        BoomstickNetwork.stopReloadSound(recruit);
    }

    private Vec3 strategicAimPosition() {
        if (shouldStrategicFireMethod == null || strategicFirePositionMethod == null) return null;
        try {
            if (!Boolean.TRUE.equals(shouldStrategicFireMethod.invoke(recruit))) return null;
            Object position = strategicFirePositionMethod.invoke(recruit);
            return position instanceof BlockPos blockPos ? Vec3.atCenterOf(blockPos) : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private void holdMusketUp() {
        if (!recruit.isUsingItem()) recruit.startUsingItem(InteractionHand.MAIN_HAND);
    }

    private void switchToMusket() {
        if (switchMainHandItemMethod == null) return;
        try {
            Predicate<ItemStack> predicate = stack -> stack.is(BoomstickMod.MUSKET.get());
            switchMainHandItemMethod.invoke(recruit, predicate);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static Method findMethod(Class<?> type, String name, Class<?>... parameters) {
        try {
            return type.getMethod(name, parameters);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
