package io.github.junner.slowfallcountdown.mixin;

import io.github.junner.slowfallcountdown.config.SlowfallCountdownConfig;
import io.github.junner.slowfallcountdown.util.ColorUtils;
import io.github.junner.slowfallcountdown.util.DelayUtil;
import io.github.junner.slowfallcountdown.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileHitMixin {
	private static final int SLOW_FALLING_SECONDS = 30;
	private static final int COUNTDOWN_SECONDS = 10;

	@Shadow public abstract void setSound(SoundEvent sound);

	@Inject(method = "onEntityHit", at = @At("HEAD"))
	private void onEntityHitHead(EntityHitResult entityHitResult, CallbackInfo ci) {
		ProjectileEntity projectile = (ProjectileEntity) (Object) this;
		if (!(projectile.getOwner() instanceof PlayerEntity owner)) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null || client.player == null || owner.getUuid() == null) {
			return;
		}

		if (!owner.getUuid().equals(client.player.getUuid())) {
			return;
		}

		Entity hitEntity = entityHitResult.getEntity();
		if (!SlowfallCountdownConfig.targetType.matches(hitEntity)) {
			return;
		}

		if (!(hitEntity instanceof LivingEntity target)) {
			return;
		}

		if (target.getUuid().equals(client.player.getUuid())) {
			return;
		}

		this.setSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER);
		UUID localPlayerUuid = client.player.getUuid();
		UUID targetUuid = target.getUuid();
		int targetEntityId = target.getId();
		DelayUtil.scheduleCountdown(
				() -> isCountdownTargetActive(localPlayerUuid, targetUuid, targetEntityId),
				remainingSeconds -> ChatUtil.sendMsg(ColorUtils.aqua + "\247l Slow Falling Countdown: " + ColorUtils.reset + remainingSeconds + ".."),
				() -> ChatUtil.sendMsg(ColorUtils.aqua + "\247l SlowfallTimer: " + ColorUtils.reset + "Reslowfall the enemy!"),
				SLOW_FALLING_SECONDS,
				COUNTDOWN_SECONDS
		);
	}

	private boolean isCountdownTargetActive(UUID localPlayerUuid, UUID targetUuid, int targetEntityId) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null || client.player == null || !client.player.getUuid().equals(localPlayerUuid)) {
			return false;
		}

		Entity target = client.world.getEntityById(targetEntityId);
		return target != null
				&& target.getUuid().equals(targetUuid)
				&& SlowfallCountdownConfig.targetType.matches(target);
	}


	@Inject(method = "onEntityHit", at = @At("TAIL"))
	private void onEntityHitTail(EntityHitResult entityHitResult, CallbackInfo ci) {
		this.setSound(SoundEvents.ENTITY_ARROW_HIT);
	}
}
