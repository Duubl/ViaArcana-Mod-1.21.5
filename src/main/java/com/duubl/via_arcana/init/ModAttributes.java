package com.duubl.via_arcana.init;

import com.duubl.via_arcana.ViaArcana;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = 
        DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, ViaArcana.MODID);

    public static final Holder<Attribute> MAGIC_DAMAGE = 
        ATTRIBUTES.register("magic_damage", () -> new RangedAttribute("attribute.name.generic.magic_damage", 0.0D, 0.0D, 5000.0D));

    public static final Holder<Attribute> KNOCKBACK = 
        ATTRIBUTES.register("knockback", () -> new RangedAttribute("attribute.name.generic.knockback", 0.0D, 0.0D, 10.0D));
    
    public static final Holder<Attribute> CRITICAL_STRIKE_CHANCE = 
        ATTRIBUTES.register("critical_strike_chance", () -> new RangedAttribute("attribute.name.generic.critical_strike_chance", 0.0D, 0.0D, 1.0D));
    
    public static final Holder<Attribute> PROJECTILE_SPEED = 
        ATTRIBUTES.register("projectile_speed", () -> new RangedAttribute("attribute.name.generic.projectile_speed", 1.0D, 0.1D, 10.0D));
    
    public static final Holder<Attribute> MANA_COST = 
        ATTRIBUTES.register("mana_cost", () -> new RangedAttribute("attribute.name.generic.mana_cost", 0.0D, 0.0D, 100.0D));
    
    public static final Holder<Attribute> CAST_SPEED = 
        ATTRIBUTES.register("cast_speed", () -> new RangedAttribute("attribute.name.generic.cast_speed", 1.0D, 0.1D, 100.0D));

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
