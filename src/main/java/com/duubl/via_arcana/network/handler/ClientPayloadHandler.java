package com.duubl.via_arcana.network.handler;

import com.duubl.via_arcana.network.packets.LeapPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    public static void handleDataOnMain(final LeapPacket data, final IPayloadContext context) {
        // No client-side handling required for LeapPacket
    }
}
