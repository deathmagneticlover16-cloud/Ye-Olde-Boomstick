package com.deathmagneticlover16.boomstick;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

public final class MusketBall extends ThrowableItemProjectile {
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
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide) {
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
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }
}
