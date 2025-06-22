package com.duubl.via_arcana.network.packets;

import com.duubl.via_arcana.ViaArcana;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PlayAnimationPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayAnimationPacket> TYPE = new CustomPacketPayload.Type<>(
        ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "weapon_slam_packet")
    );

    public static final StreamCodec<FriendlyByteBuf, PlayAnimationPacket> CODEC = StreamCodec.of(
        PlayAnimationPacket::encode,
        PlayAnimationPacket::decode
    );

    @Override
    public CustomPacketPayload.Type<PlayAnimationPacket> type() {
        return TYPE;
    }

    public static void encode(FriendlyByteBuf buf, PlayAnimationPacket msg) {
        // No data to encode for this packet
    }

    public static PlayAnimationPacket decode(FriendlyByteBuf buf) {
        return new PlayAnimationPacket();
    }
}
