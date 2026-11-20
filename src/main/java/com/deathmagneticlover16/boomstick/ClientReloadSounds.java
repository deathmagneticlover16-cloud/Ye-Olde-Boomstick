package com.deathmagneticlover16.boomstick;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public final class ClientReloadSounds {
    private static final Map<Integer, MusketReloadSoundInstance> ACTIVE = new HashMap<>();

    private ClientReloadSounds() {}

    public static void handle(int entityId, boolean start) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        MusketReloadSoundInstance previous = ACTIVE.remove(entityId);
        if (previous != null) minecraft.getSoundManager().stop(previous);
        if (!start) return;

        Entity source = minecraft.level.getEntity(entityId);
        if (source == null) return;
        MusketReloadSoundInstance sound = new MusketReloadSoundInstance(source);
        ACTIVE.put(entityId, sound);
        minecraft.getSoundManager().play(sound);
    }

    static void finished(int entityId, MusketReloadSoundInstance sound) {
        ACTIVE.remove(entityId, sound);
    }
}
