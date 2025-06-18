package com.duubl.via_arcana.init;

import com.duubl.via_arcana.ViaArcana;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = 
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ViaArcana.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DAMAGE = DATA_COMPONENTS.register(
        "damage", () -> DataComponentType.<Float>builder()
            .persistent(Codec.FLOAT)
            .networkSynchronized(ByteBufCodecs.FLOAT)
            .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> PROJECTILE_SPEED = DATA_COMPONENTS.register(
        "projectile_speed", () -> DataComponentType.<Float>builder()
            .persistent(Codec.FLOAT)
            .networkSynchronized(ByteBufCodecs.FLOAT)
            .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> KNOCKBACK = DATA_COMPONENTS.register(
        "knockback", () -> DataComponentType.<Float>builder()
            .persistent(Codec.FLOAT)
            .networkSynchronized(ByteBufCodecs.FLOAT)
            .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> CRITICAL_STRIKE_CHANCE = DATA_COMPONENTS.register(
        "critical_strike_chance", () -> DataComponentType.<Float>builder()
            .persistent(Codec.FLOAT)
            .networkSynchronized(ByteBufCodecs.FLOAT)
            .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MANA_COST = DATA_COMPONENTS.register(
        "mana_cost", () -> DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT)
            .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CAST_SPEED = DATA_COMPONENTS.register(
        "cast_speed", () -> DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT)
            .build()
    );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
} 