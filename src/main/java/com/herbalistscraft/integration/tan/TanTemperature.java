package com.herbalistscraft.integration.tan;

import net.minecraft.world.entity.player.Player;

/** Reflective bridge to Tough As Nails temperature and thirst, used only when TAN is present. */
final class TanTemperature {
    private TanTemperature() {}

    static float adjust(Player player, float amount) {
        try {
            Class<?> temperature = Class.forName("toughasnails.temperature.TemperatureHelper");
            Object result = temperature.getMethod("modifyTemperature", Player.class, float.class)
                    .invoke(null, player, amount);
            return result instanceof Float value ? value : amount;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return 0.0F;
        }
    }

    static float hydrate(Player player, float amount) {
        try {
            Class<?> thirst = Class.forName("toughasnails.thirst.ThirstHelper");
            Object result = thirst.getMethod("addThirst", Player.class, float.class).invoke(null, player, amount);
            return result instanceof Float value ? value : amount;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return 0.0F;
        }
    }
}
