package com.deathmagneticlover16.boomstick;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class MusketItem extends Item {
    private static final String LOADED = "Loaded";
    private static final String RELOAD_TICKS = "ReloadTicks";
    private static final String FIRING = "Firing";
    private static final String FIRE_AT = "FireAt";
    private static final int IGNITION_TICKS = 8;
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
        player.startUsingItem(hand);
        if (!level.isClientSide) {
            stack.getOrCreateTag().putBoolean("Shouldered", false);
            if (isLoaded(stack)) {
                if (!stack.getOrCreateTag().getBoolean(FIRING)) {
                    stack.getOrCreateTag().putBoolean(FIRING, true);
                    stack.getOrCreateTag().putLong(FIRE_AT, level.getGameTime() + IGNITION_TICKS);
                    playReport((ServerLevel) level, player);
                }
            } else {
                stack.getOrCreateTag().putInt(RELOAD_TICKS, 300 + level.random.nextInt(101));
            }
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingTicks) {
        if (level.isClientSide) return;
        int elapsed = getUseDuration(stack) - remainingTicks;
        if (stack.getOrCreateTag().getBoolean(FIRING)) {
            tryFire((ServerLevel) level, user, stack);
        } else if (!isLoaded(stack) && !stack.getOrCreateTag().getBoolean(FIRING)
                && stack.getOrCreateTag().getInt(RELOAD_TICKS) > 0
                && elapsed >= stack.getOrCreateTag().getInt(RELOAD_TICKS)) {
            setLoaded(stack, true);
            stack.getOrCreateTag().remove(RELOAD_TICKS);
            user.stopUsingItem();
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide && entity instanceof LivingEntity living && stack.getOrCreateTag().getBoolean(FIRING)) {
            tryFire((ServerLevel) level, living, stack);
        }
    }

    private void tryFire(ServerLevel level, LivingEntity user, ItemStack stack) {
        if (level.getGameTime() < stack.getOrCreateTag().getLong(FIRE_AT)) return;
        stack.getOrCreateTag().putBoolean(FIRING, false);
        stack.getOrCreateTag().remove(FIRE_AT);
        setLoaded(stack, false);
        fire(level, user);
        if (user instanceof ServerPlayer player) player.awardStat(Stats.ITEM_USED.get(this));
        if (user.isUsingItem() && user.getUseItem() == stack) user.stopUsingItem();
    }

    @Override public int getUseDuration(ItemStack stack) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? attributes : super.getDefaultAttributeModifiers(slot);
    }

    public static boolean isLoaded(ItemStack stack) {
        return !stack.hasTag() || !stack.getTag().contains(LOADED) || stack.getTag().getBoolean(LOADED);
    }

    public static void setLoaded(ItemStack stack, boolean loaded) {
        stack.getOrCreateTag().putBoolean(LOADED, loaded);
    }

    public static void fire(ServerLevel level, LivingEntity shooter) {
        MusketBall ball = new MusketBall(level, shooter);
        Vec3 look = shooter.getLookAngle();
        ball.setPos(shooter.getEyePosition().add(look.scale(0.8D)));
        ball.shoot(look.x, look.y, look.z, 4.0F, 8.0F);
        level.addFreshEntity(ball);
        level.gameEvent(shooter, GameEvent.PROJECTILE_SHOOT, shooter.position());
        SmokeEffects.spawn(level, shooter, look);
    }

    public static void playReport(ServerLevel level, LivingEntity shooter) {
        long seed = level.random.nextLong();
        for (ServerPlayer listener : level.players()) {
            double distance = listener.distanceTo(shooter);
            SoundEvent sound = distance >= 40.0D ? BoomstickMod.MUSKET_FIRE_DISTANT.get() : BoomstickMod.MUSKET_FIRE_CLOSE.get();
            float volume = distance >= 40.0D ? 8.0F : 4.0F;
            listener.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.PLAYERS,
                    shooter.getX(), shooter.getY(), shooter.getZ(), volume, 1.0F, seed));
        }
    }
}
