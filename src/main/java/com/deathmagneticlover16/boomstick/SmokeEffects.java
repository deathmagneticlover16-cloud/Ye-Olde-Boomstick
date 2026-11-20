package com.deathmagneticlover16.boomstick;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.WeakHashMap;

final class SmokeEffects {
    private static final Map<ServerLevel, ArrayDeque<Shot>> RECENT_SHOTS = new WeakHashMap<>();

    private SmokeEffects() {}

    static void spawn(ServerLevel level, Vec3 muzzle, Vec3 direction) {
        ArrayDeque<Shot> shots = RECENT_SHOTS.computeIfAbsent(level, ignored -> new ArrayDeque<>());
        long now = level.getGameTime();
        while (!shots.isEmpty() && shots.peekFirst().time < now - 200L) shots.removeFirst();
        shots.addLast(new Shot(now, muzzle));
        long nearbyShots = shots.stream().filter(shot -> shot.position.distanceToSqr(muzzle) <= 1024.0D).count();
        int smokeCount = Math.min(52, 8 + (int) nearbyShots * 2);

        level.sendParticles(BoomstickMod.MUSKET_SMOKE.get(), muzzle.x, muzzle.y, muzzle.z,
                smokeCount, 0.20D, 0.13D, 0.20D, 0.025D);

        for (int i = 0; i < 5; i++) {
            Vec3 sparkVelocity = direction.scale(0.22D + level.random.nextDouble() * 0.18D)
                    .add((level.random.nextDouble() - 0.5D) * 0.12D,
                            (level.random.nextDouble() - 0.25D) * 0.11D,
                            (level.random.nextDouble() - 0.5D) * 0.12D);
            level.sendParticles(BoomstickMod.MUSKET_SPARK.get(), muzzle.x, muzzle.y, muzzle.z,
                    0, sparkVelocity.x, sparkVelocity.y, sparkVelocity.z, 1.0D);
        }
    }

    private record Shot(long time, Vec3 position) {}
}
