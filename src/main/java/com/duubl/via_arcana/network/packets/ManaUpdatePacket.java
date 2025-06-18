package com.duubl.via_arcana.network.packets;

import com.duubl.via_arcana.ViaArcana;
import com.duubl.via_arcana.magic.ManaComponent;
import com.duubl.via_arcana.magic.ManaComponentAttachment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ManaUpdatePacket(UUID playerUuid, int mana, int maxMana) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ManaUpdatePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "mana_update_packet"));

    public static final StreamCodec<FriendlyByteBuf, ManaUpdatePacket> CODEC = StreamCodec.of(
            ManaUpdatePacket::encode,
            ManaUpdatePacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(FriendlyByteBuf buf, ManaUpdatePacket packet) {
        buf.writeUUID(packet.playerUuid());
        buf.writeInt(packet.mana());
        buf.writeInt(packet.maxMana());
    }

    private static ManaUpdatePacket decode(FriendlyByteBuf buf) {
        UUID playerUuid = buf.readUUID();
        int mana = buf.readInt();
        int maxMana = buf.readInt();
        return new ManaUpdatePacket(playerUuid, mana, maxMana);
    }

    public static class Handler {
        public static void handle(ManaUpdatePacket message, IPayloadContext context) {
            context.enqueueWork(() -> {
                // Handle on client side (this packet is sent from server to client)
                Player player = context.player(); // Get the local player on the client
                if (player != null && player.getUUID().equals(message.playerUuid())) {
                    ManaComponent manaComponent = player.getData(ManaComponentAttachment.MANA_COMPONENT);
                    if (manaComponent == null) {
                        manaComponent = new ManaComponent(message.mana(), message.maxMana(), 0.5f);
                        player.setData(ManaComponentAttachment.MANA_COMPONENT, manaComponent);
                    } else {
                        // Update both mana and maxMana
                        ManaComponent newComponent = new ManaComponent(
                            message.mana(),
                            message.maxMana(),
                            manaComponent.getRegenRate()
                        );
                        player.setData(ManaComponentAttachment.MANA_COMPONENT, newComponent);
                    }
                }
            });
        }
    }
}
