package io.github.junner.slowfallcountdown.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SlowfallCountdownConfig {
    public static TargetType targetType = TargetType.PLAYERS;

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
}
