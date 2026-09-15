package com.herbalistscraft.mixing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * What an unnamed brew actually contains. Experimental medicines are ordinary items carrying
 * this component: the properties are computed from the inputs, so nothing new is ever registered
 * at runtime and two identical experiments always produce the same bottle.
 */
public record MixtureData(float potency, int toxin, Map<String, Float> properties, boolean failure, String note) {
    public static final Codec<MixtureData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("potency", 0.5F).forGetter(MixtureData::potency),
            Codec.INT.optionalFieldOf("toxin", 0).forGetter(MixtureData::toxin),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).optionalFieldOf("properties", Map.of())
                    .forGetter(MixtureData::properties),
            Codec.BOOL.optionalFieldOf("failure", false).forGetter(MixtureData::failure),
            Codec.STRING.optionalFieldOf("note", "").forGetter(MixtureData::note)
    ).apply(instance, MixtureData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MixtureData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MixtureData decode(RegistryFriendlyByteBuf buffer) {
            float potency = buffer.readFloat();
            int toxin = buffer.readVarInt();
            int size = buffer.readVarInt();
            Map<String, Float> properties = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) {
                properties.put(buffer.readUtf(), buffer.readFloat());
            }
            boolean failure = buffer.readBoolean();
            String note = buffer.readUtf();
            return new MixtureData(potency, toxin, properties, failure, note);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, MixtureData value) {
            buffer.writeFloat(value.potency());
            buffer.writeVarInt(value.toxin());
            buffer.writeVarInt(value.properties().size());
            for (Map.Entry<String, Float> entry : value.properties().entrySet()) {
                buffer.writeUtf(entry.getKey());
                buffer.writeFloat(entry.getValue());
            }
            buffer.writeBoolean(value.failure());
            buffer.writeUtf(value.note());
        }
    };

    public boolean isEmpty() {
        return properties.isEmpty() && toxin <= 0;
    }
}
