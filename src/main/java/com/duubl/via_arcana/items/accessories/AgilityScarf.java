package com.duubl.via_arcana.items.accessories;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

public class AgilityScarf extends Accessory {
    public AgilityScarf(Properties properties) {
        super(properties);
    }

    public static void performLeap(ServerPlayer player, Vec3 direction) {
        if (player.onGround() || player.getAbilities().flying) {
            Vec3 leapVector = direction.normalize().scale(0.8);
            leapVector = leapVector.add(0, 0.4, 0);
            player.setDeltaMovement(leapVector);
            player.hurtMarked = true;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.via_arcana.agility_scarf").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("usage.tooltip.via_arcana.double_tap").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }
}
