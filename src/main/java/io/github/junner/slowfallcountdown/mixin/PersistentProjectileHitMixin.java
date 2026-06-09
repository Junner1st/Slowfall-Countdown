package io.github.junner.slowfallcountdown.mixin;

import io.github.junner.slowfallcountdown.util.ColorUtils;
import io.github.junner.slowfallcountdown.util.DelayUtil;
import io.github.junner.slowfallcountdown.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileHitMixin {
	@Shadow public abstract void setSound(SoundEvent sound);

	@Unique private ClientPlayerEntity projectileDing$clientPlayer;

	@Inject(method = "onEntityHit", at = @At("HEAD"))
	private void onEntityHitHead(EntityHitResult entityHitResult, CallbackInfo ci) {
		if (this.projectileDing$clientPlayer == null) {
			this.projectileDing$clientPlayer = MinecraftClient.getInstance().player;
		}

		Entity target = entityHitResult.getEntity();
		if (target instanceof LivingEntity) {
			ProjectileEntity projectile = (ProjectileEntity) (Object) this;
			if (this.projectileDing$clientPlayer.equals(projectile.getOwner())) {
				this.setSound(SoundEvents.ENTITY_ARROW_HIT_PLAYER);

				DelayUtil.schedule(() -> ChatUtil.sendMsg(ColorUtils.aqua + "\247l SlowfallTimer: " + ColorUtils.reset + "Reslowfall the enemy!"), 30000);
			}
		}
	}


	@Inject(method = "onEntityHit", at = @At("TAIL"))
	private void onEntityHitTail(EntityHitResult entityHitResult, CallbackInfo ci) {
		this.setSound(SoundEvents.ENTITY_ARROW_HIT);
	}
}
