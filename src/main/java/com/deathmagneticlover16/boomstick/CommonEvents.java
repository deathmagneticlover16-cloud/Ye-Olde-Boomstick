package com.deathmagneticlover16.boomstick;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID)
public final class CommonEvents {
    private static final Set<Mob> CONFIGURED_RECRUITS = Collections.newSetFromMap(new WeakHashMap<>());

    private CommonEvents() {}

    @SubscribeEvent
    public static void addRecruitMusketGoal(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Mob mob)) return;
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
        if (id != null && id.getNamespace().equals("recruits") && id.getPath().contains("crossbow")
                && CONFIGURED_RECRUITS.add(mob)) {
            mob.goalSelector.addGoal(2, new MusketRecruitGoal(mob));
        }
    }
}
