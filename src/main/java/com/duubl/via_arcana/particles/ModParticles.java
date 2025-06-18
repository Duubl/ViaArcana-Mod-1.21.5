package com.duubl.via_arcana.particles;

import java.util.function.Supplier;

import com.duubl.via_arcana.ViaArcana;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = 
        DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ViaArcana.MODID);

    public static final Supplier<SimpleParticleType> COLORED_MAGIC_PARTICLE = PARTICLES.register(
        "colored_magic_particle", 
        () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}