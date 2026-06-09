package io.github.junner.slowfallcountdown.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class SlowfallCountdownConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("slowfallcountdown.json");

    public static boolean calculatorEnabled = true;
    public static int calculationTimeSeconds = 30;
    public static int countdownSeconds = 5;
    public static TargetType targetType = TargetType.PLAYERS;

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data == null) {
                save();
                return;
            }

            calculatorEnabled = data.calculatorEnabled;
            calculationTimeSeconds = Math.max(1, data.calculationTimeSeconds);
            countdownSeconds = Math.max(0, data.countdownSeconds);
            targetType = data.targetType == null ? TargetType.PLAYERS : data.targetType;
        } catch (IOException | RuntimeException exception) {
            System.err.println("Failed to load SlowfallCountdown config: " + exception.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(new ConfigData(), writer);
            }
        } catch (IOException exception) {
            System.err.println("Failed to save SlowfallCountdown config: " + exception.getMessage());
        }
    }

    public static int getCalculationTimeSeconds() {
        return Math.max(1, calculationTimeSeconds);
    }

    public static int getCountdownSeconds() {
        if (countdownSeconds <= 0) {
            return 0;
        }

        return Math.min(countdownSeconds, getCalculationTimeSeconds());
    }

    public static boolean isPlayerOnlyTarget() {
        return targetType == TargetType.PLAYERS;
    }

    public static void setPlayerOnlyTarget(boolean playerOnlyTarget) {
        targetType = playerOnlyTarget ? TargetType.PLAYERS : TargetType.LIVING_ENTITIES;
    }

    public enum TargetType {
        PLAYERS,
        LIVING_ENTITIES;

        public boolean matches(Entity entity) {
            return switch (this) {
                case PLAYERS -> entity instanceof PlayerEntity;
                case LIVING_ENTITIES -> entity instanceof LivingEntity;
            };
        }
    }

    private static class ConfigData {
        boolean calculatorEnabled = SlowfallCountdownConfig.calculatorEnabled;
        int calculationTimeSeconds = SlowfallCountdownConfig.calculationTimeSeconds;
        int countdownSeconds = SlowfallCountdownConfig.countdownSeconds;
        TargetType targetType = SlowfallCountdownConfig.targetType;
    }
}
