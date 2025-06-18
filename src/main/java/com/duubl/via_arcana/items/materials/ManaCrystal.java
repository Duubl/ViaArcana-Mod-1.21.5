package com.duubl.via_arcana.items.materials;

import com.duubl.via_arcana.magic.ManaComponent;
import com.duubl.via_arcana.magic.ManaComponentAttachment;
import com.duubl.via_arcana.network.packets.ManaUpdatePacket;
import com.duubl.via_arcana.sounds.ModSounds;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Consumer;

public class ManaCrystal extends Item {
    private static final int MANA_INCREASE = 20; // Amount to increase max mana by

    public ManaCrystal(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ManaComponent manaComponent = player.getData(ManaComponentAttachment.MANA_COMPONENT);
            if (manaComponent != null) {
                // Check if player has already reached the mana cap
                if (manaComponent.getMaxMana() >= ManaComponent.getManaCap()) {
                    return InteractionResult.FAIL;
                }

                // Only increase max mana, keep current mana the same
                int newMaxMana = Math.min(manaComponent.getMaxMana() + MANA_INCREASE, ManaComponent.getManaCap());
                ManaComponent newComponent = new ManaComponent(
                    manaComponent.getMana(), // Keep current mana the same
                    newMaxMana,
                    manaComponent.getRegenRate()
                );
                player.setData(ManaComponentAttachment.MANA_COMPONENT, newComponent);
                
                // Consume the item
                ItemStack stack = player.getItemInHand(hand);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                
                // Send feedback message
                player.displayClientMessage(Component.literal("Maximum mana increased to " + newMaxMana)
                    .withStyle(ChatFormatting.AQUA), true);

                // Sync the new mana values to the client
                if (player instanceof ServerPlayer serverPlayer) {
                    PacketDistributor.sendToPlayer(serverPlayer,
                        new ManaUpdatePacket(player.getUUID(), newComponent.getMana(), newComponent.getMaxMana()));
                }
            }
        }
        float randomPitch = 0.75f + level.getRandom().nextFloat() * 0.5f; // Random between 0.75 and 1.25
        player.playSound(ModSounds.SPELL_CAST_1.get(), 0.25f, randomPitch);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.via_arcana.increase_mana").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
    }
}
