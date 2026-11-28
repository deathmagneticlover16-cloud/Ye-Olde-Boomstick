package com.deathmagneticlover16.boomstick;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public final class ClientGunshotSounds {
    private ClientGunshotSounds() {}

    public static void play(double x, double y, double z, float closeVolume, float closePitch,
                            float distantVolume, float distantPitch,
                            ResourceLocation closeSoundId, ResourceLocation distantSoundId) {
        SoundEvent closeSound = ForgeRegistries.SOUND_EVENTS.getValue(closeSoundId);
        SoundEvent distantSound = ForgeRegistries.SOUND_EVENTS.getValue(distantSoundId);
        if (closeSound == null || distantSound == null) return;
        float dominantVolume = Math.max(closeVolume, distantVolume);
        float layerThreshold = dominantVolume * 0.05F;
        if (closeVolume > 0.0F && closeVolume >= layerThreshold) {
            Minecraft.getInstance().getSoundManager().play(new MusketGunshotSoundInstance(
                    closeSound, x, y, z, closeVolume, closePitch));
        }
        if (distantVolume > 0.0F && distantVolume >= layerThreshold) {
            Minecraft.getInstance().getSoundManager().play(new MusketGunshotSoundInstance(
                    distantSound, x, y, z, distantVolume, distantPitch));
        }
    }
}
