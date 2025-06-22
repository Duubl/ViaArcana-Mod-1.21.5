package com.duubl.via_arcana.network.packets;

import com.duubl.via_arcana.ViaArcana;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AnimationBroadcastPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AnimationBroadcastPacket> TYPE = new CustomPacketPayload.Type<>(
        ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "animation_broadcast_packet")
    );

    public static final StreamCodec<FriendlyByteBuf, AnimationBroadcastPacket> CODEC = StreamCodec.of(
        AnimationBroadcastPacket::encode,
        AnimationBroadcastPacket::decode
    );

    @Override
    public CustomPacketPayload.Type<AnimationBroadcastPacket> type() {
        return TYPE;
    }

    public static void encode(FriendlyByteBuf buf, AnimationBroadcastPacket msg) {
        // No data to encode
    }

    public static AnimationBroadcastPacket decode(FriendlyByteBuf buf) {
        return new AnimationBroadcastPacket();
    }
}
