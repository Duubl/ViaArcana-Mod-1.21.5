package com.duubl.via_arcana.items.weapons.magic;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.duubl.via_arcana.init.ModDataComponents;

public abstract class MagicWeapon extends Item implements MutableDataComponentHolder {
    public static final DeferredRegister.DataComponents REGISTRAR = 
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "via_arcana");

    private final PatchedDataComponentMap components;

    private ParticleOptions trailParticle;
    private ParticleOptions impactParticle;

    public MagicWeapon(Properties properties) {
        super(properties.stacksTo(1));
        this.components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
        
        // Set default values using the registered components
        this.set(ModDataComponents.DAMAGE, 0.0f);
        this.set(ModDataComponents.PROJECTILE_SPEED, 0.0f);
        this.set(ModDataComponents.KNOCKBACK, 0.0f);
        this.set(ModDataComponents.CRITICAL_STRIKE_CHANCE, 0.0f);
        this.set(ModDataComponents.MANA_COST, 0);
        this.set(ModDataComponents.CAST_SPEED, 0);
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
        float castSpeed = getCastSpeed();
        // Convert castSpeed to seconds (castSpeed=60 -> 1 second)
        float cooldownSeconds = castSpeed / 60.0f;
        player.getCooldowns().addCooldown(this.getDefaultInstance(), Math.round(cooldownSeconds * 20.0f)); // Convert to ticks
    }

    public int getCastSpeed() {
        return this.get(ModDataComponents.CAST_SPEED);
    }

    public void setCastSpeed(int speed) {
        this.set(ModDataComponents.CAST_SPEED, speed);
    }

    @Override
    public DataComponentMap getComponents() {
        return this.components;
    }

    @Nullable
    @Override
    public <T> T remove(DataComponentType<? extends T> componentType) {
        return this.components.remove(componentType);
    }

    @Override
    public void applyComponents(DataComponentPatch patch) {
        this.components.applyPatch(patch);
    }

    @Override
    public void applyComponents(DataComponentMap components) {
        this.components.setAll(components);
    }

    @Nullable
    @Override
    public <T> T get(DataComponentType<? extends T> componentType) {
        return this.components.get(componentType);
    }

    @Override
    public <T> @org.jetbrains.annotations.Nullable T set(DataComponentType<T> componentType,
            @org.jetbrains.annotations.Nullable T arg0) {
        return this.components.set(componentType, arg0);
    }

    // Getters and setters for all properties
    public float getDamage() {
        return this.get(ModDataComponents.DAMAGE);
    }

    public void setDamage(float damage) {
        this.set(ModDataComponents.DAMAGE, damage);
    }

    public float getProjectileSpeed() {
        return this.get(ModDataComponents.PROJECTILE_SPEED);
    }

    public void setProjectileSpeed(float speed) {
        this.set(ModDataComponents.PROJECTILE_SPEED, speed);
    }

    public float getKnockback() {
        return this.get(ModDataComponents.KNOCKBACK);
    }

    public void setKnockback(float knockback) {
        this.set(ModDataComponents.KNOCKBACK, knockback);
    }

    public float getCriticalStrikeChance() {
        return this.get(ModDataComponents.CRITICAL_STRIKE_CHANCE);
    }

    public void setCriticalStrikeChance(float chance) {
        this.set(ModDataComponents.CRITICAL_STRIKE_CHANCE, chance);
    }

    public int getManaCost() {
        return this.get(ModDataComponents.MANA_COST);
    }

    public void setManaCost(int cost) {
        this.set(ModDataComponents.MANA_COST, cost);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.empty());
        // Damage tooltip
        float damage = getDamage();
        tooltipComponents.accept(Component.literal("Damage: " + damage)
            .withStyle(ChatFormatting.GRAY));

        // Projectile Speed tooltip
        float speed = getProjectileSpeed();
        tooltipComponents.accept(Component.literal("Projectile Speed: " + speed)
            .withStyle(ChatFormatting.GRAY));

        // Knockback tooltip
        float knockback = getKnockback();
        tooltipComponents.accept(Component.literal("Knockback: " + knockback)
            .withStyle(ChatFormatting.GRAY));

        // Critical Strike Chance tooltip
        float critChance = getCriticalStrikeChance();
        tooltipComponents.accept(Component.literal("Critical Strike Chance: " + (critChance * 100) + "%")
            .withStyle(ChatFormatting.GRAY));

        // Mana Cost tooltip
        int manaCost = getManaCost();
        tooltipComponents.accept(Component.literal("Mana Cost: " + manaCost)
            .withStyle(ChatFormatting.GRAY));

        // Cast Speed tooltip
        int castSpeed = getCastSpeed();
        tooltipComponents.accept(Component.literal("Cast Speed: " + (castSpeed / 60.0f) + "s")
            .withStyle(ChatFormatting.GRAY));
    }
}