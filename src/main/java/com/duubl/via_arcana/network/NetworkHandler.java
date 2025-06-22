package com.duubl.via_arcana.network;

import com.duubl.via_arcana.network.packets.LeapPacket;
import com.duubl.via_arcana.network.packets.PlayAnimationPacket;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
public class NetworkHandler {
    public static void sendToServer(LeapPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(PlayAnimationPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayer(ServerPlayer player, PlayAnimationPacket packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}
