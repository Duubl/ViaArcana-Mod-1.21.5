package com.duubl.via_arcana;

import com.duubl.via_arcana.client.ClientSetup;
import com.duubl.via_arcana.entities.ModEntities;
import com.duubl.via_arcana.init.ModAttributes;
import com.duubl.via_arcana.items.ModItems;
import com.duubl.via_arcana.magic.ManaComponent;
import com.duubl.via_arcana.network.handler.ManaEventHandler;
import com.duubl.via_arcana.network.packets.LeapPacket;
import com.duubl.via_arcana.network.handler.ServerPayloadHandler;
import com.duubl.via_arcana.network.packets.ManaUpdatePacket;
import com.duubl.via_arcana.particles.ColoredMagicParticle;
import com.duubl.via_arcana.particles.ModParticles;
import com.duubl.via_arcana.sounds.ModSounds;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static com.duubl.via_arcana.items.ModItems.ITEMS;
import com.duubl.via_arcana.magic.ManaComponentAttachment;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ViaArcana.MODID)
public class ViaArcana
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "via_arcana";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    // Creates a new creative mode tab
    public static final DeferredRegister<CreativeModeTab> ACCESSORIES_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<CreativeModeTab> MATERIALS_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<CreativeModeTab> MAGIC_WEAPONS_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ManaComponent>> MANA_DATA_COMPONENT =
            DATA_COMPONENTS.register("mana", () -> DataComponentType.<ManaComponent>builder()
                    .persistent(ManaComponent.CODEC) // Codec for saving to NBT
                    .networkSynchronized(ManaComponent.STREAM_CODEC) // StreamCodec for network sync
                    .build());

    // Creates a creative tab with the id "via_arcana:via_arcana_accessories" that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ACCESSORIES = ACCESSORIES_TAB.register("via_arcana_accessories", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.via_arcana_accessories"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.HASTE_TREADS.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.HASTE_TREADS.get());
                output.accept(ModItems.MOON_SHOES.get());
                output.accept(ModItems.MOONSTRIDER_BOOTS.get());
                output.accept(ModItems.SNOW_SHOES.get());
                output.accept(ModItems.AGILITY_SCARF.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS = MATERIALS_TAB.register("via_arcana_materials", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.via_arcana_materials"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.MANA_SHARD.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.MANA_SHARD.get());
                output.accept(ModItems.MANA_CRYSTAL.get());
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGIC_WEAPONS = MAGIC_WEAPONS_TAB.register("via_arcana_magic_weapons", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.via_arcana_magic_weapons"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.WAND_OF_SPLINTERS.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.WAND_OF_SPLINTERS.get());
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ViaArcana(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        ACCESSORIES_TAB.register(modEventBus);
        MATERIALS_TAB.register(modEventBus);
        MAGIC_WEAPONS_TAB.register(modEventBus);

        DATA_COMPONENTS.register(modEventBus);
        ManaComponentAttachment.register(modEventBus);
        ManaEventHandler.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModParticles.register(modEventBus);
        ModAttributes.register(modEventBus);

        modEventBus.addListener(ClientSetup::onClientSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ViaArcana) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register network handler
        modEventBus.addListener(this::registerNetwork);

        // Register the item to a creative tab
//        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.COLORED_MAGIC_PARTICLE.get(), ColoredMagicParticle.Provider::new);
        }
    }

    // Add the example block item to the building blocks tab
//    private void addCreative(BuildCreativeModeTabContentsEvent event)
//    {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
//            event.accept(ModItems.HASTE_TREADS);
//    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    private void registerNetwork(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                LeapPacket.TYPE,
                LeapPacket.CODEC,
                ServerPayloadHandler::handle
        );
        registrar.playToClient(
                ManaUpdatePacket.TYPE,
                ManaUpdatePacket.CODEC,
                ManaUpdatePacket.Handler::handle
        );
    }
}
