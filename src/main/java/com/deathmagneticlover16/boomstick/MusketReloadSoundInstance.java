package com.deathmagneticlover16.boomstick;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public final class MusketReloadSoundInstance extends AbstractTickableSoundInstance {
    private final Entity source;

    public MusketReloadSoundInstance(Entity source) {
        super(BoomstickMod.MUSKET_RELOAD.get(), SoundSource.PLAYERS, RandomSource.create());
        this.source = source;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.1F;
        this.pitch = 1.0F;
        updatePosition();
    }

    @Override
    public void tick() {
        if (!source.isAlive() || source.isRemoved()) {
            stop();
            ClientReloadSounds.finished(source.getId(), this);
            return;
        }
        updatePosition();
    }

    private void updatePosition() {
        this.x = source.getX();
        this.y = source.getY();
        this.z = source.getZ();
    }
}
