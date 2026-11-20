package com.deathmagneticlover16.boomstick;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID)
public final class CommonEvents {
    private CommonEvents() {}

    @SubscribeEvent
    public static void addRecruitMusketGoal(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Mob mob)) return;
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
        if (id != null && id.getNamespace().equals("recruits") && id.getPath().contains("crossbow")
                && !mob.getPersistentData().getBoolean("YeOldeBoomstickGoalAdded")) {
            mob.goalSelector.addGoal(2, new MusketRecruitGoal(mob));
            mob.getPersistentData().putBoolean("YeOldeBoomstickGoalAdded", true);
        }
    }
}
