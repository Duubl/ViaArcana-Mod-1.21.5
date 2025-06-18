package com.duubl.via_arcana.network.handler;

import com.duubl.via_arcana.items.accessories.AgilityScarf;
import com.duubl.via_arcana.network.packets.LeapPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    public static void handle(final LeapPacket packet, final IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player != null) {
                    AgilityScarf.performLeap(player, packet.direction());
                }
            });
        }
    }
}
