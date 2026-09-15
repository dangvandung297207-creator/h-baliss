package com.herbalistscraft.medicine;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/** The four forms a finished medicine takes. */
public enum MedicineKind implements StringRepresentable {
    TONIC,
    TEA,
    SALVE,
    OIL;

    public static final Codec<MedicineKind> CODEC = StringRepresentable.fromEnum(MedicineKind::values);

    @Override
    public String getSerializedName() {
        return name();
    }

    public String translationKey() {
        return "kind." + HerbalistsCraft.MODID + "." + name();
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }
}
