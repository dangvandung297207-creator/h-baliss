package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Reserved for future mod-defined advancement triggers. All advancements currently use the
 * {@code minecraft:impossible} criterion and are granted by {@code AdvancementGrants}.
 */
public final class ModCriteria {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, HerbalistsCraft.MODID);

    private ModCriteria() {}
}
