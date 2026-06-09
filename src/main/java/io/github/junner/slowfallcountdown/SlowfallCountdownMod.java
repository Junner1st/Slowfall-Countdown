package io.github.junner.slowfallcountdown;

import io.github.junner.slowfallcountdown.config.SlowfallCountdownConfig;
import net.fabricmc.api.ModInitializer;

public class SlowfallCountdownMod implements ModInitializer {
    @Override
    public void onInitialize() {
        SlowfallCountdownConfig.load();
    }
}
