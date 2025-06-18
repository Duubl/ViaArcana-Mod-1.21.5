package com.duubl.via_arcana.items.accessories;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.function.Consumer;

public class MoonShoes extends Accessory {
    public MoonShoes(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (slotContext.identifier().equals("accessory")) {
            // Apply Jump Boost I effect (amplifier 0 = level 1, duration 200 ticks = 10 seconds)
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 200, 0, false, false, false));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (slotContext.identifier().equals("accessory")) {
            entity.removeEffect(MobEffects.JUMP_BOOST);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.via_arcana.jump_boost").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }
}
