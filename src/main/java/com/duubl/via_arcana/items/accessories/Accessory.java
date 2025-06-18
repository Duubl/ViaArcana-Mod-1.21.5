package com.duubl.via_arcana.items.accessories;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public abstract class Accessory extends Item implements ICurioItem {
    public Accessory(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        // Ensure the slot is "accessory"
        if (!slotContext.identifier().equals("accessory")) {
            return false;
        }

        // Allow multiple instances if specified by subclass
        if (allowMultipleEquipped()) {
            return true;
        }

        // Check if the same item is already equipped in any "accessory" slot
        LivingEntity entity = slotContext.entity();
        return CuriosApi.getCuriosInventory(entity).map(handler -> {
            return handler.findCurios(stack.getItem()).isEmpty(); // No matching item found, allow equipping
        }).orElse(true); // No curios inventory, allow equipping
    }

    protected boolean allowMultipleEquipped() {
        return false;
    }
}
