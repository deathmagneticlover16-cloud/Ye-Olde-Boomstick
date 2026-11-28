package com.deathmagneticlover16.boomstick;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
            // Recruits' built-in priority-0 crossbow goal claims LOOK during strategic fire,
            // even when the held item is not a crossbow. Run first so the musket goal is not starved.
            mob.goalSelector.addGoal(-1, new MusketRecruitGoal(mob));
        }
    }

    @SubscribeEvent
    public static void preventCreativeMusketBlockBreaking(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().getAbilities().instabuild
                && event.getEntity().getMainHandItem().getItem() instanceof MusketItem) {
            event.setCanceled(true);
        }
    }
}
