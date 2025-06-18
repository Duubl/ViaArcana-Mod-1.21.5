package com.duubl.via_arcana.magic;

import com.duubl.via_arcana.ViaArcana;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ManaComponentAttachment {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ViaArcana.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ManaComponent>> MANA_COMPONENT =
            ATTACHMENT_TYPES.register(
                    "mana",
                    () -> AttachmentType.builder(ManaComponent::new)
                            .serialize(ManaComponent.CODEC)
                            .build()
            );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
