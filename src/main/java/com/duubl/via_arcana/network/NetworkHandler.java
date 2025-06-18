package com.duubl.via_arcana.network;

import com.duubl.via_arcana.network.packets.LeapPacket;
import net.neoforged.neoforge.network.PacketDistributor;
public class NetworkHandler {
    public static void sendToServer(LeapPacket packet) {
        PacketDistributor.sendToServer(packet);
    }
}
