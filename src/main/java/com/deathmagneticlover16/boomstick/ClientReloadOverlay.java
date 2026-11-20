package com.deathmagneticlover16.boomstick;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID, value = Dist.CLIENT)
public final class ClientReloadOverlay {
    private static final ResourceLocation GUI_ICONS = new ResourceLocation("textures/gui/icons.png");
    private static final int[] STAGE_COLORS = {
            0xD63A35, 0xF07C2E, 0xF2C744, 0x9ACD45, 0x32A852
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
        graphics.blit(GUI_ICONS, left, top, 0, 84, 182, 5);

        int filledWidth = Math.round(progress * 183.0F);
        if (filledWidth > 0) {
            int stage = Math.min(4, (int) (progress * 5.0F));
            int color = STAGE_COLORS[stage];
            graphics.setColor(((color >> 16) & 0xFF) / 255.0F,
                    ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
            graphics.blit(GUI_ICONS, left, top, 0, 89, filledWidth, 5);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
