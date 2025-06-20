package com.duubl.via_arcana.items.weapons.melee;

import java.util.Set;
import com.duubl.via_arcana.init.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class MeleeWeapon extends Item {

    public MeleeWeapon(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Get values from attributes instead of data components
        double knockback = attacker.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double critChance = attacker.getAttributeValue(ModAttributes.CRITICAL_STRIKE_CHANCE);
        
        // Apply critical strike
        if (attacker.getRandom().nextFloat() < critChance) {
            // Critical hit effects
        }
        
        // Apply knockback
        if (knockback > 0) {
            target.knockback((float)knockback, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return this.getDefaultAbilities().contains(itemAbility);
    }

    public Set<ItemAbility> getDefaultAbilities() {
        return Set.of(ItemAbilities.SWORD_SWEEP);
    }
}
