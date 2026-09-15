package com.herbalistscraft.knowledge;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/** Pages torn from a herbalist's journal: they teach recipes when read. */
public enum JournalPageKind implements StringRepresentable {
    HERBAL("herbal", 2),
    MEDICINAL("medicinal", 3),
    TOXIC("toxic", 2),
    ANCIENT("ancient", 5);

    public static final Codec<JournalPageKind> CODEC = StringRepresentable.fromEnum(JournalPageKind::values);

    private final String name;
    private final int recipes;

    JournalPageKind(String name, int recipes) {
        this.name = name;
        this.recipes = recipes;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /** How many recipes a page teaches when read at a Herbalist's Table. */
    public int recipes() {
        return recipes;
    }

    public String translationKey() {
        return "item." + HerbalistsCraft.MODID + ".journal_page_" + name;
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }
}
