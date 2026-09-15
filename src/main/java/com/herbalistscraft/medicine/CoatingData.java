package com.herbalistscraft.medicine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * The oil smeared on a weapon: which medicine, how many hits are left, when it was applied and
 * how long it lasts. Nothing permanent, nothing stackable - the component simply runs out.
 */
public record CoatingData(ResourceLocation medicine, int charges, int maxCharges, long appliedAt, int durationTicks) {
    public static final Codec<CoatingData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("medicine").forGetter(CoatingData::medicine),
            Codec.INT.fieldOf("charges").forGetter(CoatingData::charges),
            Codec.INT.fieldOf("max_charges").forGetter(CoatingData::maxCharges),
            Codec.LONG.fieldOf("applied_at").forGetter(CoatingData::appliedAt),
            Codec.INT.fieldOf("duration").forGetter(CoatingData::durationTicks)
    ).apply(instance, CoatingData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CoatingData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CoatingData decode(RegistryFriendlyByteBuf buffer) {
            return new CoatingData(buffer.readResourceLocation(), buffer.readVarInt(), buffer.readVarInt(),
                    buffer.readVarLong(), buffer.readVarInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, CoatingData value) {
            buffer.writeResourceLocation(value.medicine());
            buffer.writeVarInt(value.charges());
            buffer.writeVarInt(value.maxCharges());
            buffer.writeVarLong(value.appliedAt());
            buffer.writeVarInt(value.durationTicks());
        }
    };

    public CoatingData spent(int hits) {
        return new CoatingData(medicine, charges - hits, maxCharges, appliedAt, durationTicks);
    }

    public boolean exhausted(long gameTime) {
        return charges <= 0 || (durationTicks > 0 && gameTime - appliedAt > durationTicks);
    }
}
