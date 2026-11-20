package com.deathmagneticlover16.boomstick;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientGunshotSounds {
    private ClientGunshotSounds() {}

    public static void play(boolean distant, double x, double y, double z, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(new MusketGunshotSoundInstance(
                distant ? BoomstickMod.MUSKET_FIRE_DISTANT.get() : BoomstickMod.MUSKET_FIRE_CLOSE.get(),
                x, y, z, volume, pitch));
    }
}
