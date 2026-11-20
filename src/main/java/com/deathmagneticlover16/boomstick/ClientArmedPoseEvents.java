package com.deathmagneticlover16.boomstick;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BoomstickMod.MOD_ID, value = Dist.CLIENT)
public final class ClientArmedPoseEvents {
    private ClientArmedPoseEvents() {}

    @SubscribeEvent
    public static void forceRecruitCrossbowHold(RenderLivingEvent.Pre<?, ?> event) {
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
        if (entityId == null || !entityId.getNamespace().equals("recruits")
                || !entityId.getPath().contains("crossbow")) return;

        ItemStack stack = event.getEntity().getMainHandItem();
        boolean shouldered = stack.hasTag() && stack.getTag().getBoolean("Shouldered");
        if (!stack.is(BoomstickMod.MUSKET.get()) || !MusketItem.isArmed(stack)
                || MusketItem.isReloading(stack) || shouldered
                || !(event.getRenderer().getModel() instanceof HumanoidModel<?> model)) return;

        if (event.getEntity().getMainArm() == HumanoidArm.RIGHT) {
            model.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
            model.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        } else {
            model.leftArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
            model.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        }
    }
}
