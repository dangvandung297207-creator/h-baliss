package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.knowledge.Knowledge;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Permanent player knowledge, saved with the player and synced to their client. */
public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, HerbalistsCraft.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Knowledge>> KNOWLEDGE =
            ATTACHMENT_TYPES.register("knowledge", () -> AttachmentType.builder(Knowledge::new)
                    .serialize(Knowledge.CODEC)
                    .sync(Knowledge.STREAM_CODEC)
                    .copyOnDeath()
                    .build());

    /** Accumulated toxin load. Synced so the client can show the HUD indicator. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> TOXIN =
            ATTACHMENT_TYPES.register("toxin", () -> AttachmentType.builder(() -> 0)
                    .serialize(com.mojang.serialization.Codec.INT)
                    .sync(net.minecraft.network.codec.ByteBufCodecs.VAR_INT)
                    .build());

    private ModAttachments() {}
}
