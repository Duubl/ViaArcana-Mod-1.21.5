package com.duubl.via_arcana.items.weapons.magic;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.core.particles.ParticleOptions;
import com.duubl.via_arcana.init.ModAttributes;

public abstract class MagicWeapon extends Item {

    private ParticleOptions trailParticle;
    private ParticleOptions impactParticle;

    public MagicWeapon(Properties properties) {
        super(properties.stacksTo(1));
    }

    public ParticleOptions getTrailParticle() { return trailParticle; }

    public ParticleOptions getImpactParticle() { return impactParticle; }

    public void setTrailParticle(ParticleOptions particle) { this.trailParticle = particle; }
    
    public void setImpactParticle(ParticleOptions particle) { this.impactParticle = particle; }

    protected boolean isOnCooldown(Player player) {
        return player.getCooldowns().isOnCooldown(this.getDefaultInstance());
    }

    protected float getCurrentCooldown(Player player) {
        return player.getCooldowns().getCooldownPercent(this.getDefaultInstance(), 0.0f);
    }

    protected void startCooldown(Player player) {
        // Calculate cooldown based on castSpeed: lower castSpeed = shorter cooldown
        double castSpeed = getCastSpeedFromItem(this.getDefaultInstance());
        // Convert castSpeed to seconds (castSpeed=60 -> 1 second)
        double cooldownSeconds = castSpeed / 60.0f;
        player.getCooldowns().addCooldown(this.getDefaultInstance(), (int) Math.round(cooldownSeconds * 20.0f)); // Convert to ticks
    }

    public int getManaCostFromItem(ItemStack weaponStack) {
        final double[] totalManaCost = {0.0};
        
        weaponStack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
            if (attribute.is(ModAttributes.MANA_COST)) {
                totalManaCost[0] += modifier.amount();
            }
        });
        
        return Math.max(0, (int) totalManaCost[0]); // Ensure non-negative
    }

    public double getCastSpeedFromItem(ItemStack weaponStack) {
        final double[] totalCastSpeed = {0.0};
        
        weaponStack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
            if (attribute.is(ModAttributes.CAST_SPEED)) {
                totalCastSpeed[0] += modifier.amount();
            }
        });
        
        return totalCastSpeed[0];
    }
}