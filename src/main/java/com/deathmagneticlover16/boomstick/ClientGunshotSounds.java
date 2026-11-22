package com.deathmagneticlover16.boomstick;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientGunshotSounds {
    private ClientGunshotSounds() {}

    public static void play(double x, double y, double z, float closeVolume, float closePitch,
                            float distantVolume, float distantPitch) {
        float dominantVolume = Math.max(closeVolume, distantVolume);
        float layerThreshold = dominantVolume * 0.05F;
        if (closeVolume > 0.0F && closeVolume >= layerThreshold) {
            Minecraft.getInstance().getSoundManager().play(new MusketGunshotSoundInstance(
                    BoomstickMod.MUSKET_FIRE_CLOSE.get(), x, y, z, closeVolume, closePitch));
        }
        if (distantVolume > 0.0F && distantVolume >= layerThreshold) {
            Minecraft.getInstance().getSoundManager().play(new MusketGunshotSoundInstance(
                    BoomstickMod.MUSKET_FIRE_DISTANT.get(), x, y, z, distantVolume, distantPitch));
        }
    }
}
