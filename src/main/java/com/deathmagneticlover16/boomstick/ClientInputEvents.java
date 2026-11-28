package com.deathmagneticlover16.boomstick;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID, value = Dist.CLIENT)
public final class ClientInputEvents {
    private ClientInputEvents() {}

    @SubscribeEvent
    public static void fireMusket(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        InteractionHand hand = player.getMainHandItem().getItem() instanceof MusketItem
                ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack musket = player.getItemInHand(hand);
        if (!(musket.getItem() instanceof MusketItem) || !MusketItem.isArmed(musket)) return;

        BoomstickNetwork.requestFire(hand);
        event.setSwingHand(false);
        event.setCanceled(true);
    }
}
