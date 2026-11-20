package com.deathmagneticlover16.boomstick;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.WeakHashMap;

final class SmokeEffects {
    private static final Map<ServerLevel, ArrayDeque<Shot>> RECENT_SHOTS = new WeakHashMap<>();

    private SmokeEffects() {}

    static void spawn(ServerLevel level, LivingEntity shooter, Vec3 direction) {
        ArrayDeque<Shot> shots = RECENT_SHOTS.computeIfAbsent(level, ignored -> new ArrayDeque<>());
        long now = level.getGameTime();
        Vec3 muzzle = shooter.getEyePosition().add(direction.scale(1.2D));
        while (!shots.isEmpty() && shots.peekFirst().time < now - 200L) shots.removeFirst();
        shots.addLast(new Shot(now, muzzle));
        long nearbyShots = shots.stream().filter(shot -> shot.position.distanceToSqr(muzzle) <= 1024.0D).count();
        int smokeCount = Math.min(80, 10 + (int) nearbyShots * 3);

        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, muzzle.x, muzzle.y, muzzle.z,
                smokeCount, 0.28D, 0.20D, 0.28D, 0.018D);
        level.sendParticles(ParticleTypes.POOF, muzzle.x, muzzle.y, muzzle.z,
                Math.min(20, 5 + (int) nearbyShots), 0.15D, 0.12D, 0.15D, 0.03D);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, muzzle.x, muzzle.y, muzzle.z,
                3, 0.06D, 0.06D, 0.06D, 0.10D);
    }

    private record Shot(long time, Vec3 position) {}
}
