package com.duubl.via_arcana.network.handler;

import com.duubl.via_arcana.magic.ManaComponent;
import com.duubl.via_arcana.magic.ManaComponentAttachment;
import com.duubl.via_arcana.network.packets.ManaUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class ManaEventHandler {

    public static void register(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(new ManaEventHandler());
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        // Skip if on client side
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        // Get player and mana component
        Player player = event.getEntity();
        ManaComponent manaComponent = player.getData(ManaComponentAttachment.MANA_COMPONENT);
        
        // Initialize mana component if it's missing
        if (manaComponent == null) {
            manaComponent = new ManaComponent();
            player.setData(ManaComponentAttachment.MANA_COMPONENT, manaComponent);
        }

        // Store old mana value to check if it changed
        int oldMana = manaComponent.getMana();
        
        // Update mana regeneration
        manaComponent.tick();
        
        // Only send update if mana actually changed
        if (oldMana != manaComponent.getMana()) {
            PacketDistributor.sendToPlayer((ServerPlayer) player,
                new ManaUpdatePacket(player.getUUID(), manaComponent.getMana(), manaComponent.getMaxMana()));
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        // Skip if player is dead (death event)
        if (!event.isWasDeath()) {
            return;
        }

        // Get the old and new player
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        // Get mana components
        ManaComponent oldMana = oldPlayer.getData(ManaComponentAttachment.MANA_COMPONENT);
        ManaComponent newMana = newPlayer.getData(ManaComponentAttachment.MANA_COMPONENT);

        // Initialize new mana component if missing
        if (newMana == null) {
            newMana = new ManaComponent();
            newPlayer.setData(ManaComponentAttachment.MANA_COMPONENT, newMana);
        }

        // Copy old values if available
        if (oldMana != null) {
            ManaComponent copiedMana = new ManaComponent(
                oldMana.getMana(),
                oldMana.getMaxMana(),
                oldMana.getRegenRate()
            );
            newPlayer.setData(ManaComponentAttachment.MANA_COMPONENT, copiedMana);
        }

        // Sync with client
        if (newPlayer instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new ManaUpdatePacket(newPlayer.getUUID(), newPlayer.getData(ManaComponentAttachment.MANA_COMPONENT).getMana(), newPlayer.getData(ManaComponentAttachment.MANA_COMPONENT).getMaxMana()));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggingIn(PlayerEvent.PlayerLoggedInEvent event) {
        // Get player and mana component
        Player player = event.getEntity();
        ManaComponent manaComponent = player.getData(ManaComponentAttachment.MANA_COMPONENT);

        // Initialize mana component if missing
        if (manaComponent == null) {
            manaComponent = new ManaComponent();
            player.setData(ManaComponentAttachment.MANA_COMPONENT, manaComponent);
        }

        // Sync with client
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new ManaUpdatePacket(player.getUUID(), manaComponent.getMana(), manaComponent.getMaxMana()));
        }
    }

    // Add new event handler for client-side initialization
    @SubscribeEvent
    public void onClientPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            ManaComponent manaComponent = player.getData(ManaComponentAttachment.MANA_COMPONENT);
            if (manaComponent == null) {
                manaComponent = new ManaComponent();
                player.setData(ManaComponentAttachment.MANA_COMPONENT, manaComponent);
            }
        }
    }
}
