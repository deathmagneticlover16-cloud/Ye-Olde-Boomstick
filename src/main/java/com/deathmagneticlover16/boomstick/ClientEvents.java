package com.deathmagneticlover16.boomstick;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BoomstickMod.MUSKET_BALL_ENTITY.get(), context -> new ThrownItemRenderer<>(context, 0.6F, true));
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BoomstickMod.MUSKET_SMOKE.get(), MusketSmokeParticle.Provider::new);
        event.registerSpriteSet(BoomstickMod.MUSKET_SPARK.get(), MusketSparkParticle.Provider::new);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(BoomstickMod.MUSKET.get(), BoomstickMod.id("armed"),
                    (stack, level, entity, seed) -> MusketItem.isArmed(stack) ? 1.0F : 0.0F);
            ItemProperties.register(BoomstickMod.MUSKET.get(), BoomstickMod.id("shouldered"),
                    (stack, level, entity, seed) -> stack.hasTag() && stack.getTag().getBoolean("Shouldered") ? 1.0F : 0.0F);
        });
    }
}
