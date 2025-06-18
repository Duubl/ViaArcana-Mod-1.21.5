package com.duubl.via_arcana.magic;

import com.duubl.via_arcana.ViaArcana;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.function.Function;

@EventBusSubscriber(modid = ViaArcana.MODID, value = Dist.CLIENT)
public class ManaBarOverlay {
    private static final ResourceLocation MANA_BAR = ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "textures/gui/mana_bar.png");
    private static final int SYMBOL_WIDTH = 6;  // Width of each mana symbol (30/5 symbols)
    private static final int SYMBOL_HEIGHT = 9; // Height of each mana symbol
    private static final int TEXTURE_WIDTH = 30; // Total width of texture
    private static final int TEXTURE_HEIGHT = 9; // Height of texture
    private static final int SYMBOLS_PER_ROW = 10; // Number of symbols to display per row
    private static final int MANA_PER_SYMBOL = 20; // Amount of mana each symbol represents

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        // Only check for creative mode, remove screen check
        if (player.getAbilities().instabuild) return;

        ManaComponent manaComponent = player.getData(ManaComponentAttachment.MANA_COMPONENT);
        if (manaComponent == null) return;

        // Show mana bar if player has at least one mana bar worth of mana
        if (manaComponent.getMaxMana() >= MANA_PER_SYMBOL) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int screenHeight = minecraft.getWindow().getGuiScaledHeight();

            int maxMana = manaComponent.getMaxMana();
            int currentMana = manaComponent.getMana();
            
            // Calculate the exact number of symbols needed (no rounding up)
            int totalSymbols = maxMana / MANA_PER_SYMBOL;
            
            int rows = (totalSymbols + SYMBOLS_PER_ROW - 1) / SYMBOLS_PER_ROW; // Round up to nearest row

            // Calculate starting position (centered)
            int startX = (screenWidth - (Math.min(totalSymbols, SYMBOLS_PER_ROW) * SYMBOL_WIDTH)) / 2;
            int startY = screenHeight - 49 - ((rows - 1) * (SYMBOL_HEIGHT + 2)); // 2 pixels between rows

            // Draw each mana symbol
            for (int i = 0; i < totalSymbols; i++) {
                int row = i / SYMBOLS_PER_ROW;
                int col = i % SYMBOLS_PER_ROW;
                int x = startX + (col * SYMBOL_WIDTH);
                int y = startY + (row * (SYMBOL_HEIGHT + 2));

                // Calculate which symbol to use based on remaining mana
                int remainingMana = currentMana - (i * MANA_PER_SYMBOL);
                int symbolIndex;
                if (remainingMana <= 0) {
                    symbolIndex = 0; // Empty (0 mana)
                } else if (remainingMana < 5) {
                    symbolIndex = 1; // Quarter (1-5 mana)
                } else if (remainingMana < 10) {
                    symbolIndex = 2; // Half (6-10 mana)
                } else if (remainingMana < 15) {
                    symbolIndex = 3; // Three quarters (11-15 mana)
                } else {
                    symbolIndex = 4; // Full (16-20 mana)
                }

                // Draw the symbol
                guiGraphics.blit((Function<ResourceLocation, RenderType>) RenderType::guiTextured, 
                    MANA_BAR, 
                    x, y, 
                    symbolIndex * SYMBOL_WIDTH, 0, 
                    SYMBOL_WIDTH, SYMBOL_HEIGHT, 
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
        }
    }
}
