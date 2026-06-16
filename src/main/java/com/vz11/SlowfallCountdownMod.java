package com.vz11;

import com.vz11.config.SlowfallCountdownConfig;
import net.fabricmc.api.ModInitializer;

public class SlowfallCountdownMod implements ModInitializer {
    @Override
    public void onInitialize() {
        SlowfallCountdownConfig.load();
    }
}
