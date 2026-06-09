package io.github.junner.slowfallcountdown.client;

import io.github.junner.slowfallcountdown.config.SlowfallCountdownConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SlowfallCountdownClient implements ClientModInitializer {
    public static final KeyBinding TOGGLE_CALCULATOR_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.slowfallcountdown.toggle_calculator",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.slowfallcountdown"
    ));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_CALCULATOR_KEYBIND.wasPressed()) {
                SlowfallCountdownConfig.calculatorEnabled = !SlowfallCountdownConfig.calculatorEnabled;
                SlowfallCountdownConfig.save();
            }
        });
    }
}
