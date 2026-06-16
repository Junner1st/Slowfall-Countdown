package com.vz11.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.vz11.client.SlowfallCountdownClient;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.entries.EmptyEntry;
import me.shedaniel.clothconfig2.gui.widget.SearchFieldEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

import java.util.List;

public class SlowfallCountdownModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SlowfallCountdownModMenuIntegration::createConfigScreen;
    }

    private static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.slowfallcountdown.config"));

        builder.setGlobalized(false);
        builder.setAfterInitConsumer(SlowfallCountdownModMenuIntegration::removeSearchField);
        builder.setSavingRunnable(SlowfallCountdownConfig::save);

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.slowfallcountdown.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("option.slowfallcountdown.calculator_enabled"),
                        SlowfallCountdownConfig.calculatorEnabled
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.slowfallcountdown.calculator_enabled.tooltip"))
                .setSaveConsumer(value -> SlowfallCountdownConfig.calculatorEnabled = value)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("option.slowfallcountdown.reset_on_local_death"),
                        SlowfallCountdownConfig.resetOnLocalPlayerDeath
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.slowfallcountdown.reset_on_local_death.tooltip"))
                .setSaveConsumer(value -> SlowfallCountdownConfig.resetOnLocalPlayerDeath = value)
                .build());

        general.addEntry(entryBuilder.fillKeybindingField(
                        Text.translatable("option.slowfallcountdown.toggle_keybind"),
                        SlowfallCountdownClient.TOGGLE_CALCULATOR_KEYBIND
                )
                .setDefaultValue(SlowfallCountdownClient.TOGGLE_CALCULATOR_KEYBIND.getDefaultKey())
                .setTooltip(Text.translatable("option.slowfallcountdown.toggle_keybind.tooltip"))
                .setKeySaveConsumer(key -> {
                    SlowfallCountdownClient.TOGGLE_CALCULATOR_KEYBIND.setBoundKey(key);
                    KeyBinding.updateKeysByCode();
                    MinecraftClient.getInstance().options.write();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(
                        Text.translatable("option.slowfallcountdown.calculation_time"),
                        SlowfallCountdownConfig.calculationTimeSeconds
                )
                .setDefaultValue(30)
                .setMin(1)
                .setTooltip(Text.translatable("option.slowfallcountdown.calculation_time.tooltip"))
                .setSaveConsumer(value -> SlowfallCountdownConfig.calculationTimeSeconds = Math.max(1, value))
                .build());

        general.addEntry(entryBuilder.startIntField(
                        Text.translatable("option.slowfallcountdown.countdown_time"),
                        SlowfallCountdownConfig.countdownSeconds
                )
                .setDefaultValue(5)
                .setMin(0)
                .setTooltip(Text.translatable("option.slowfallcountdown.countdown_time.tooltip"))
                .setSaveConsumer(value -> SlowfallCountdownConfig.countdownSeconds = Math.max(0, value))
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("option.slowfallcountdown.player_only_target"),
                        SlowfallCountdownConfig.isPlayerOnlyTarget()
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.slowfallcountdown.player_only_target.tooltip"))
                .setSaveConsumer(SlowfallCountdownConfig::setPlayerOnlyTarget)
                .build());

        return builder.build();
    }

    private static void removeSearchField(Screen screen) {
        if (!(screen instanceof ClothConfigScreen clothConfigScreen)) {
            return;
        }

        List<?> entries = clothConfigScreen.listWidget.children();
        for (int index = 0; index < entries.size(); index++) {
            if (!(entries.get(index) instanceof SearchFieldEntry)) {
                continue;
            }

            entries.remove(index);
            if (index > 0 && entries.get(index - 1) instanceof EmptyEntry) {
                entries.remove(index - 1);
                index--;
            }
            if (index < entries.size() && entries.get(index) instanceof EmptyEntry) {
                entries.remove(index);
            }
            return;
        }
    }
}
