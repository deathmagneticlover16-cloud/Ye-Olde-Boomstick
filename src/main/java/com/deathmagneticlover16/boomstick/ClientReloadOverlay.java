package com.deathmagneticlover16.boomstick;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID, value = Dist.CLIENT)
public final class ClientReloadOverlay {
    private static final int[] STAGE_COLORS = {
            0xFFD63A35, 0xFFF07C2E, 0xFFF2C744, 0xFF9ACD45, 0xFF32A852
    };

    private ClientReloadOverlay() {}

    @SubscribeEvent
    public static void renderReloadBar(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() != VanillaGuiOverlay.EXPERIENCE_BAR.type()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = player.getUseItem();
        if (!stack.is(BoomstickMod.MUSKET.get())) return;
        float progress = MusketItem.getReloadProgress(stack, player.level());
        if (progress < 0.0F) return;

        event.setCanceled(true);
        drawBar(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(),
                event.getWindow().getGuiScaledHeight(), Mth.clamp(progress, 0.0F, 1.0F));
    }

    private static void drawBar(GuiGraphics graphics, int screenWidth, int screenHeight, float progress) {
        int left = screenWidth / 2 - 91;
        int top = screenHeight - 29;
        graphics.fill(left - 1, top - 1, left + 183, top + 6, 0xCC111111);

        for (int stage = 0; stage < 5; stage++) {
            int segmentLeft = left + stage * 37;
            int segmentRight = stage == 4 ? left + 182 : segmentLeft + 34;
            float stageProgress = Mth.clamp(progress * 5.0F - stage, 0.0F, 1.0F);
            graphics.fill(segmentLeft, top, segmentRight, top + 5, 0xFF3A3A3A);
            int filledRight = segmentLeft + Math.round((segmentRight - segmentLeft) * stageProgress);
            if (filledRight > segmentLeft) {
                graphics.fill(segmentLeft, top, filledRight, top + 5, STAGE_COLORS[stage]);
            }
        }
    }
}
