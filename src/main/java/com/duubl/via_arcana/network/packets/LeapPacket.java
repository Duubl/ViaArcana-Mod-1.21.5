package com.duubl.via_arcana.network.packets;

import com.duubl.via_arcana.ViaArcana;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.duubl.via_arcana.items.accessories.AgilityScarf;
import net.minecraft.world.phys.Vec3;

public record LeapPacket(Vec3 direction) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LeapPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "leap_packet")
    );

    public static final StreamCodec<FriendlyByteBuf, LeapPacket> CODEC = StreamCodec.of(
            LeapPacket::encode,
            LeapPacket::decode
    );

    @Override
    public CustomPacketPayload.Type<LeapPacket> type() {
        return TYPE;
    }

    public static void encode(FriendlyByteBuf buf, LeapPacket msg) {
        buf.writeDouble(msg.direction.x);
        buf.writeDouble(msg.direction.y);
        buf.writeDouble(msg.direction.z);
    }

    public static LeapPacket decode(FriendlyByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new LeapPacket(new Vec3(x, y, z));
    }

    public static void handle(LeapPacket msg, IPayloadContext ctx) {
        if (ctx.flow().isServerbound()) {
            ctx.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) ctx.player();
                if (player != null) {
                    AgilityScarf.performLeap(player, msg.direction);
                }
            });
        }
    }
}
