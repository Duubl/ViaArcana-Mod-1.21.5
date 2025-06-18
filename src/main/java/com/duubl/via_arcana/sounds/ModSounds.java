package com.duubl.via_arcana.sounds;

import com.duubl.via_arcana.ViaArcana;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = 
        DeferredRegister.create(Registries.SOUND_EVENT, ViaArcana.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELL_HIT_3 = 
        SOUND_EVENTS.register("spell_hit_3",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "spell_hit_3")));

        public static final DeferredHolder<SoundEvent, SoundEvent> SPELL_CAST_1 = 
        SOUND_EVENTS.register("spell_cast_1",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "spell_cast_1")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELL_CAST_2 = 
        SOUND_EVENTS.register("spell_cast_2",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "spell_cast_2")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELL_CAST_3 = 
        SOUND_EVENTS.register("spell_cast_3",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "spell_cast_3")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELL_CAST_4 = 
        SOUND_EVENTS.register("spell_cast_4",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "spell_cast_4")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SPELL_CAST_5 = 
        SOUND_EVENTS.register("spell_cast_5",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "spell_cast_5")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
} 