package com.deathmagneticlover16.boomstick;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BoomstickMod.MUSKET_BALL_ENTITY.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(BoomstickMod.MUSKET.get(), BoomstickMod.id("loaded"),
                    (stack, level, entity, seed) -> MusketItem.isLoaded(stack) ? 1.0F : 0.0F);
            ItemProperties.register(BoomstickMod.MUSKET.get(), BoomstickMod.id("shouldered"),
                    (stack, level, entity, seed) -> stack.hasTag() && stack.getTag().getBoolean("Shouldered") ? 1.0F : 0.0F);
        });
    }
}
