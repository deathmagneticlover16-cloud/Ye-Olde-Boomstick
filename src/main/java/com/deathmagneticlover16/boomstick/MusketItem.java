package com.deathmagneticlover16.boomstick;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public final class MusketItem extends Item {
    private static final String ARMED = "Armed";
    private static final String LEGACY_LOADED = "Loaded";
    private static final String RELOADING = "Reloading";
    private static final String RELOAD_AT = "ReloadAt";
    private static final String FIRING = "Firing";
    private static final String FIRE_AT = "FireAt";
    private static final int IGNITION_TICKS = 8;
    private static final int RELOAD_DURATION_TICKS = 300;
    private final Multimap<Attribute, AttributeModifier> attributes;

    public MusketItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Bayonet damage", 5.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Bayonet speed", -2.6D, AttributeModifier.Operation.ADDITION));
        attributes = builder.build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isArmed(stack)) return InteractionResultHolder.pass(stack);
        player.startUsingItem(hand);
        if (!level.isClientSide) {
            stack.getOrCreateTag().putBoolean("Shouldered", false);
            if (!stack.getOrCreateTag().getBoolean(RELOADING)) {
                stack.getOrCreateTag().putBoolean(RELOADING, true);
                stack.getOrCreateTag().putLong(RELOAD_AT, level.getGameTime() + RELOAD_DURATION_TICKS);
                BoomstickNetwork.startReloadSound(player);
            }
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingTicks) {
        if (level.isClientSide) return;
        if (stack.getOrCreateTag().getBoolean(FIRING)) {
            tryFire((ServerLevel) level, user, stack);
        } else if (!isArmed(stack) && stack.getOrCreateTag().getBoolean(RELOADING)
                && level.getGameTime() >= stack.getOrCreateTag().getLong(RELOAD_AT)) {
            setArmed(stack, true);
            clearReload(stack);
            BoomstickNetwork.stopReloadSound(user);
            playActionSound((ServerLevel) level, user, BoomstickMod.MUSKET_ARMED.get(), 1.0F);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!level.isClientSide && stack.getOrCreateTag().getBoolean(RELOADING)) {
            clearReload(stack);
            BoomstickNetwork.stopReloadSound(entity);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide && entity instanceof LivingEntity living) {
            if (stack.getOrCreateTag().getBoolean(FIRING)) tryFire((ServerLevel) level, living, stack);
            if (stack.getOrCreateTag().getBoolean(RELOADING)
                    && (!living.isUsingItem() || living.getUseItem() != stack)) {
                clearReload(stack);
                BoomstickNetwork.stopReloadSound(living);
            }
        }
    }

    private void tryFire(ServerLevel level, LivingEntity user, ItemStack stack) {
        if (level.getGameTime() < stack.getOrCreateTag().getLong(FIRE_AT)) return;
        stack.getOrCreateTag().putBoolean(FIRING, false);
        stack.getOrCreateTag().remove(FIRE_AT);
        setArmed(stack, false);
        fire(level, user);
        if (user instanceof ServerPlayer player) player.awardStat(Stats.ITEM_USED.get(this));
    }

    public static void beginFiring(ServerPlayer player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(BoomstickMod.MUSKET.get()) || !isArmed(stack)
                || stack.getOrCreateTag().getBoolean(FIRING)
                || stack.getOrCreateTag().getBoolean(RELOADING)) return;
        stack.getOrCreateTag().putBoolean("Shouldered", false);
        stack.getOrCreateTag().putBoolean(FIRING, true);
        stack.getOrCreateTag().putLong(FIRE_AT, player.level().getGameTime() + IGNITION_TICKS);
        playReport((ServerLevel) player.level(), player);
    }

    private static void clearReload(ItemStack stack) {
        stack.getOrCreateTag().remove(RELOADING);
        stack.getOrCreateTag().remove(RELOAD_AT);
    }

    @Override public int getUseDuration(ItemStack stack) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.CROSSBOW; }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(isArmed(stack)
                ? "tooltip.ye_olde_boomstick.armed"
                : "tooltip.ye_olde_boomstick.empty").withStyle(isArmed(stack) ? ChatFormatting.RED : ChatFormatting.GRAY));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? attributes : super.getDefaultAttributeModifiers(slot);
    }

    public static boolean isArmed(ItemStack stack) {
        if (!stack.hasTag()) return true;
        if (stack.getTag().contains(ARMED)) return stack.getTag().getBoolean(ARMED);
        if (stack.getTag().contains(LEGACY_LOADED)) return stack.getTag().getBoolean(LEGACY_LOADED);
        return true;
    }

    public static void setArmed(ItemStack stack, boolean armed) {
        stack.getOrCreateTag().putBoolean(ARMED, armed);
        stack.getOrCreateTag().remove(LEGACY_LOADED);
    }

    public static void fire(ServerLevel level, LivingEntity shooter) {
        Vec3 look = shooter.getLookAngle();
        fire(level, shooter, look);
    }

    public static void fire(ServerLevel level, LivingEntity shooter, Vec3 direction) {
        MusketBall ball = new MusketBall(level, shooter);
        Vec3 normalizedDirection = direction.normalize();
        Vec3 muzzle = muzzlePosition(shooter, normalizedDirection);
        ball.setPos(muzzle);
        ball.shoot(normalizedDirection.x, normalizedDirection.y, normalizedDirection.z, 7.0F, 3.0F);
        level.addFreshEntity(ball);
        level.gameEvent(shooter, GameEvent.PROJECTILE_SHOOT, shooter.position());
        SmokeEffects.spawn(level, muzzle, normalizedDirection);
    }

    private static Vec3 muzzlePosition(LivingEntity shooter, Vec3 forward) {
        Vec3 right = forward.cross(new Vec3(0.0D, 1.0D, 0.0D));
        if (right.lengthSqr() < 0.001D) right = new Vec3(1.0D, 0.0D, 0.0D);
        right = right.normalize();
        double handSide = shooter.getMainArm() == net.minecraft.world.entity.HumanoidArm.RIGHT ? 0.28D : -0.28D;
        return shooter.getEyePosition().add(forward.scale(1.72D)).add(right.scale(handSide)).add(0.0D, -0.22D, 0.0D);
    }

    public static void playReport(ServerLevel level, LivingEntity shooter) {
        long seed = level.random.nextLong();
        float closePitch = 0.95F + level.random.nextFloat() * 0.10F;
        float distantPitch = 0.875F + level.random.nextFloat() * 0.25F;
        for (ServerPlayer listener : level.players()) {
            double distance = listener.distanceTo(shooter);
            SoundEvent sound = distance >= 40.0D ? BoomstickMod.MUSKET_FIRE_DISTANT.get() : BoomstickMod.MUSKET_FIRE_CLOSE.get();
            float volume = distance >= 40.0D ? 8.0F : 4.0F;
            float pitch = distance >= 40.0D ? distantPitch : closePitch;
            listener.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.PLAYERS,
                    shooter.getX(), shooter.getY(), shooter.getZ(), volume, pitch, seed));
        }
    }

    public static void playActionSound(ServerLevel level, LivingEntity source, SoundEvent sound, float volume) {
        level.playSound(null, source.getX(), source.getY(), source.getZ(), sound, SoundSource.PLAYERS, volume, 1.0F);
    }
}
