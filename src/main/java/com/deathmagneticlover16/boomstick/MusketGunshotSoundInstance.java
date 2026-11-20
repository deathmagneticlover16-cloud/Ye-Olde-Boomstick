package com.deathmagneticlover16.boomstick;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MusketGunshotSoundInstance extends AbstractSoundInstance {
    public MusketGunshotSoundInstance(SoundEvent sound, double x, double y, double z, float volume, float pitch) {
        super(sound, SoundSource.PLAYERS, RandomSource.create());
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
        this.looping = false;
        this.delay = 0;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.relative = false;
    }
}
