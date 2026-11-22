package com.deathmagneticlover16.boomstick;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class MusketBall extends ThrowableItemProjectile {
    private static final double WHIZZ_RADIUS = 4.0D;
    private final Set<UUID> whizzedPlayers = new HashSet<>();

    public MusketBall(EntityType<? extends MusketBall> type, Level level) {
        super(type, level);
    }

    public MusketBall(Level level, LivingEntity shooter) {
        super(BoomstickMod.MUSKET_BALL_ENTITY.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return BoomstickMod.MUSKET_BALL.get();
    }

    @Override
    protected float getGravity() {
        return 0.01F;
    }

    @Override
    public void tick() {
        Vec3 start = position();
        super.tick();
        if (!level().isClientSide) playNearMissSounds(start, position());
    }

    private void playNearMissSounds(Vec3 start, Vec3 end) {
        Vec3 travel = end.subtract(start);
        double travelLengthSquared = travel.lengthSqr();
        if (travelLengthSquared < 0.0001D) return;

        AABB sweptArea = new AABB(start, end).inflate(WHIZZ_RADIUS);
        for (ServerPlayer player : level().getEntitiesOfClass(ServerPlayer.class, sweptArea)) {
            if (player == getOwner() || player.isSpectator() || whizzedPlayers.contains(player.getUUID())) continue;
            Vec3 eye = player.getEyePosition();
            double progress = Math.max(0.0D, Math.min(1.0D,
                    eye.subtract(start).dot(travel) / travelLengthSquared));
            Vec3 closestPoint = start.add(travel.scale(progress));
            double distance = eye.distanceTo(closestPoint);
            if (distance > WHIZZ_RADIUS) continue;

            whizzedPlayers.add(player.getUUID());
            float pitch = 0.92F + random.nextFloat() * 0.16F;
            player.connection.send(new ClientboundSoundPacket(Holder.direct(BoomstickMod.MUSKET_WHIZZ.get()),
                    SoundSource.PLAYERS, closestPoint.x, closestPoint.y, closestPoint.z,
                    0.9F, pitch, random.nextLong()));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide) {
            if (result.getEntity() instanceof ServerPlayer player) whizzedPlayers.add(player.getUUID());
            float damage = 24.0F;
            if (result.getEntity() instanceof LivingEntity target) {
                var entityId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
                if (entityId != null && entityId.getNamespace().equals("recruits") && target.getArmorValue() < 5) {
                    damage = Math.max(damage, (target.getHealth() + target.getAbsorptionAmount()) * 1.35F + 4.0F);
                }
            }
            result.getEntity().hurt(damageSources().thrown(this, getOwner()), damage);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!(level() instanceof ServerLevel serverLevel)) return;

        BlockState state = serverLevel.getBlockState(result.getBlockPos());
        if (state.isAir()) return;
        Vec3 hit = result.getLocation();
        Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal()).scale(0.04D);
        Vec3 particlePosition = hit.add(normal);
        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(result.getBlockPos()),
                particlePosition.x, particlePosition.y, particlePosition.z,
                7, 0.11D, 0.11D, 0.11D, 0.06D);

        SoundType sound = state.getSoundType(serverLevel, result.getBlockPos(), this);
        serverLevel.playSound(null, hit.x, hit.y, hit.z, sound.getBreakSound(), SoundSource.BLOCKS,
                0.55F, 1.15F + random.nextFloat() * 0.20F);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }
}
