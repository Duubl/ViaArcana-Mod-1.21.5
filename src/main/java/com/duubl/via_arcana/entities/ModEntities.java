package com.duubl.via_arcana.entities;

import java.util.function.Supplier;

import com.duubl.via_arcana.ViaArcana;
import com.duubl.via_arcana.entities.projectiles.BaseSpellProjectile;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = 
        DeferredRegister.createEntities(ViaArcana.MODID);

    public static final Supplier<EntityType<BaseSpellProjectile>> BASE_SPELL_PROJECTILE = 
        ENTITY_TYPES.register("base_spell_projectile", () -> EntityType.Builder.<BaseSpellProjectile>of(BaseSpellProjectile::new, MobCategory.MISC)
        .sized(0.25f, 0.25f)
        .noSummon()
        .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "base_spell_projectile"))));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
} 