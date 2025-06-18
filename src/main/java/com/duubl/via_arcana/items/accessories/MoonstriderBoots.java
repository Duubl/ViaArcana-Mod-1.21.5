package com.duubl.via_arcana.items.accessories;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.function.Consumer;

public class MoonstriderBoots extends Accessory {
    private static final DustParticleOptions SPEED_PARTICLE = new DustParticleOptions(14811135, 1.0f);

    public MoonstriderBoots(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        Level level = entity.level();
        if (slotContext.identifier().equals("accessory")) {
            // Apply Speed I effect (amplifier 0 = level 1, duration 200 ticks = 10 seconds)
            // Apply Jump Boost I effect (amplifier 0 = level 1, duration 200 ticks = 10 seconds)
            entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 0, false, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 200, 0, false, false, false));
        }

        // Spawn particles on client side only
        if (level.isClientSide && entity.isSprinting() && slotContext.visible()) {
            // Randomly spawn 1-2 particles per tick near player's feet
            if (level.random.nextFloat() < 0.6f) { // 60% chance per tick
                double x = entity.getX() + (level.random.nextDouble() - 0.5) * 0.5; // Small spread around feet
                double y = entity.getY() + 0.3; // Slightly above feet
                double z = entity.getZ() + (level.random.nextDouble() - 0.5) * 0.5;
                level.addParticle(SPEED_PARTICLE, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (slotContext.identifier().equals("accessory")) {
            entity.removeEffect(MobEffects.SPEED);
            entity.removeEffect(MobEffects.JUMP_BOOST);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.via_arcana.speed_boots").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("tooltip.via_arcana.jump_boost").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }
}
