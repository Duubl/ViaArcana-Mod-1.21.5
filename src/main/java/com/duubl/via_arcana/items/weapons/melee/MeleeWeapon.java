package com.duubl.via_arcana.items.weapons.melee;

import java.util.Set;
import com.duubl.via_arcana.init.ModAttributes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
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
        // Get values from the weapon's attributes, not the attacker's attributes
        double knockback = getKnockbackFromWeapon(stack);
        double critChance = getCriticalStrikeChanceFromWeapon(stack);
        
        // Apply critical strike
        if (attacker.getRandom().nextFloat() < critChance) {
            if (attacker.level() instanceof ServerLevel serverLevel) {
                double x = target.getX();
                double y = target.getY() + target.getEyeHeight();
                double z = target.getZ();
                serverLevel.sendParticles(ParticleTypes.CRIT, 
                    x, y, z, 15, 0.5D, 0.5D, 0.5D, 0.1D);
            }
        }
        
        // TODO: Apply knockback only if this is a sweep attack
        // Apply knockback
        if (knockback > 0) {
            target.knockback((float)knockback, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
    }
    
    private double getKnockbackFromWeapon(ItemStack weaponStack) {
        if (weaponStack == null || weaponStack.isEmpty()) {
            return 0.0;
        }
        
        final double[] totalKnockback = {0.0};
        
        weaponStack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
            if (attribute.is(ModAttributes.KNOCKBACK)) {
                totalKnockback[0] += modifier.amount();
            }
        });
        
        return totalKnockback[0];
    }
    
    private double getCriticalStrikeChanceFromWeapon(ItemStack weaponStack) {
        if (weaponStack == null || weaponStack.isEmpty()) {
            return 0.0;
        }
        
        final double[] totalCritChance = {0.0};
        
        weaponStack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
            if (attribute.is(ModAttributes.CRITICAL_STRIKE_CHANCE)) {
                totalCritChance[0] += modifier.amount();
            }
        });
        
        return totalCritChance[0];
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return this.getDefaultAbilities().contains(itemAbility);
    }

    public Set<ItemAbility> getDefaultAbilities() {
        return Set.of(ItemAbilities.SWORD_SWEEP);
    }
}
