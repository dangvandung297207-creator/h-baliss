package com.herbalistscraft.herb;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;

/** Which generated block tag a herb accepts as ground. */
public enum HerbSoil implements StringRepresentable {
    DIRT,
    FARMLAND,
    SAND,
    MOSS,
    NETHERRACK,
    SOUL_SOIL,
    WATER_EDGE;

    public static final Codec<HerbSoil> CODEC = StringRepresentable.fromEnum(HerbSoil::values);

    @Override
    public String getSerializedName() {
        return name();
    }

    public TagKey<Block> tag() {
        return TagKey.create(Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, "herb_soils/" + getSerializedName()));
    }
}
