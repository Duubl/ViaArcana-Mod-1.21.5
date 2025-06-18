package com.duubl.via_arcana.items.accessories;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;
import java.util.function.Consumer;

public class SnowShoes extends Accessory {
    public SnowShoes(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canWalkOnPowderedSnow(SlotContext slotContext, ItemStack stack) {
        return true;

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.via_arcana.snow_shoes").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }
}
