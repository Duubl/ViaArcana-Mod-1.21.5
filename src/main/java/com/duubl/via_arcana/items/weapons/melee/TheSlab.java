package com.duubl.via_arcana.items.weapons.melee;

import com.duubl.via_arcana.init.ModAttributes;
import com.duubl.via_arcana.network.NetworkHandler;
import com.duubl.via_arcana.network.packets.PlayAnimationPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

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

    // TODO: Add charged shockwave ability on right click. 
    // Animation plays, but the player head goes invisible. Animation is also too slow. Add charge time.
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // Only trigger on main hand
        if (hand == InteractionHand.MAIN_HAND) {
            // Send packet to server to broadcast animation
            if (level.isClientSide()) {
                NetworkHandler.sendToServer(new PlayAnimationPacket());
            }
            
            // Return success to prevent item use (like eating)
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
}
