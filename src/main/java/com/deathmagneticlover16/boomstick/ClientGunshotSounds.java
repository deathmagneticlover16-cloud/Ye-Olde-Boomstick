package com.deathmagneticlover16.boomstick;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID, value = Dist.CLIENT)
public final class ClientGunshotSounds {
    private static final ArrayDeque<Report> PENDING = new ArrayDeque<>();
    private static int spacingTicks;

    private ClientGunshotSounds() {}

    public static void enqueue(double x, double y, double z, float closeVolume, float closePitch,
                               float distantVolume, float distantPitch) {
        PENDING.addLast(new Report(x, y, z, closeVolume, closePitch, distantVolume, distantPitch));
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getInstance().level == null) {
            PENDING.clear();
            spacingTicks = 0;
            return;
        }
        if (spacingTicks > 0) spacingTicks--;
        if (spacingTicks > 0 || PENDING.isEmpty()) return;

        play(PENDING.removeFirst());
        spacingTicks = 4;
    }

    private static void play(Report report) {
        float dominantVolume = Math.max(report.closeVolume, report.distantVolume);
        float layerThreshold = dominantVolume * 0.05F;
        if (report.closeVolume > 0.0F && report.closeVolume >= layerThreshold) {
            Minecraft.getInstance().getSoundManager().play(new MusketGunshotSoundInstance(
                    BoomstickMod.MUSKET_FIRE_CLOSE.get(), report.x, report.y, report.z,
                    report.closeVolume, report.closePitch));
        }
        if (report.distantVolume > 0.0F && report.distantVolume >= layerThreshold) {
            Minecraft.getInstance().getSoundManager().play(new MusketGunshotSoundInstance(
                    BoomstickMod.MUSKET_FIRE_DISTANT.get(), report.x, report.y, report.z,
                    report.distantVolume, report.distantPitch));
        }
    }

    private record Report(double x, double y, double z, float closeVolume, float closePitch,
                          float distantVolume, float distantPitch) {}
}
