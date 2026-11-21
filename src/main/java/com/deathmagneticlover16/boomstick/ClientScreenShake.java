package com.deathmagneticlover16.boomstick;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID, value = Dist.CLIENT)
public final class ClientScreenShake {
    private static int remainingTicks;

    private ClientScreenShake() {}

    public static void start() {
        remainingTicks = 6;
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || remainingTicks <= 0) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().isPaused()) return;

        float strength = remainingTicks / 6.0F;
        player.setXRot(player.getXRot() - 0.38F * strength
                + (player.getRandom().nextFloat() - 0.5F) * 0.24F * strength);
        player.setYRot(player.getYRot()
                + (player.getRandom().nextFloat() - 0.5F) * 0.36F * strength);
        remainingTicks--;
    }
}
