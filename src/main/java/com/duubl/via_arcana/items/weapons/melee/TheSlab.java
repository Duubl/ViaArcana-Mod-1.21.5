package com.duubl.via_arcana.items.weapons.melee;

import com.duubl.via_arcana.init.ModAttributes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class TheSlab extends MeleeWeapon {

    private static final float BASE_DAMAGE = 10.0f;
    private static final float BASE_KNOCKBACK = 1.0f;
    private static final float BASE_CRITICAL_STRIKE_CHANCE = 0.1f;
    private static final float BASE_ATTACK_SPEED = -3.4f;

    public TheSlab(Properties properties) {
        super(properties.attributes(createAttributes()));
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, BASE_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
        .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, BASE_ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
        .add(ModAttributes.KNOCKBACK, new AttributeModifier(ResourceLocation.withDefaultNamespace("weapon.knockback"), BASE_KNOCKBACK, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
        .add(ModAttributes.CRITICAL_STRIKE_CHANCE, new AttributeModifier(ResourceLocation.withDefaultNamespace("weapon.critical_strike_chance"), BASE_CRITICAL_STRIKE_CHANCE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
        .build();
    }
}
