package com.deathmagneticlover16.boomstick;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(BoomstickMod.MOD_ID)
public final class BoomstickMod {
    public static final String MOD_ID = "ye_olde_boomstick";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

    public static final RegistryObject<Item> MUSKET = ITEMS.register("musket", () -> new MusketItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MUSKET_BALL = ITEMS.register("musket_ball", () -> new Item(new Item.Properties()));
    public static final RegistryObject<EntityType<MusketBall>> MUSKET_BALL_ENTITY = ENTITIES.register("musket_ball",
            () -> EntityType.Builder.<MusketBall>of(MusketBall::new, MobCategory.MISC)
                    .sized(0.0625F, 0.0625F).clientTrackingRange(96).updateInterval(1).build(id("musket_ball").toString()));
    public static final RegistryObject<SoundEvent> MUSKET_FIRE_CLOSE = sound("musket_fire_close");
    public static final RegistryObject<SoundEvent> MUSKET_FIRE_DISTANT = sound("musket_fire_distant");
    public static final RegistryObject<SoundEvent> MUSKET_RELOAD = sound("musket_reload");
    public static final RegistryObject<SoundEvent> MUSKET_ARMED = sound("musket_armed");
    public static final RegistryObject<SimpleParticleType> MUSKET_SMOKE = PARTICLES.register("musket_smoke", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MUSKET_SPARK = PARTICLES.register("musket_spark", () -> new SimpleParticleType(false));

    public BoomstickMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        ENTITIES.register(bus);
        SOUNDS.register(bus);
        PARTICLES.register(bus);
        bus.addListener(this::addCreativeTabItems);
        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(BoomstickNetwork::register);
    }

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id(name)));
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(MUSKET);
            event.accept(MUSKET_BALL);
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
