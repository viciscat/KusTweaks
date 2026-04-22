package io.github.viciscat.kustweaks.mixin.omt;

import io.github.viciscat.kustweaks.injected.ExtendedProjectileTurret;
import io.github.viciscat.kustweaks.util.KusUtils;
import io.github.viciscat.kustweaks.util.Rotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import omtteam.omlib.util.RandomUtil;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.turrets.ProjectileTurret;
import omtteam.openmodularturrets.util.TurretHeadUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import techguns.entities.projectiles.GenericProjectile;

import java.util.Random;

@Mixin(value = ProjectileTurret.class, remap = false)
public abstract class ProjectileTurretMixin extends AbstractDirectedTurretMixin implements ExtendedProjectileTurret {

	@Inject(method = "forceShot", at = @At(value = "INVOKE", target = "Lomtteam/openmodularturrets/tileentity/TurretBase;setEnergyStored(ILnet/minecraft/util/EnumFacing;)V", shift = At.Shift.AFTER), cancellable = true)
	private void tryUseTGProjectileForceShot(CallbackInfoReturnable<Boolean> cir) {
		if (!kusTweaks$shouldUseTechGunsProjectile()) return;
		cir.setReturnValue(true);
		BlockPos blockPos = getPos();
		for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
			GenericProjectile projectile = kusTweaks$createTechGunsProjectile(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, yaw, pitch, 3, (float) getActualTurretAccuracy() / 200);
			getWorld().spawnEntity(projectile);
		}
		this.getWorld().playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
				OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);
		ticks = 0;
	}

	@Inject(method = "shootProjectile", at = @At(value = "INVOKE", target = "Lomtteam/openmodularturrets/tileentity/TurretBase;setEnergyStored(ILnet/minecraft/util/EnumFacing;)V", shift = At.Shift.AFTER), cancellable = true)
	private void tryUseTGProjectileShootProjectile(double adjustedX, double adjustedY, double adjustedZ, float speedFactor, float accuracy, ItemStack ammo, CallbackInfo ci) {
		if (!kusTweaks$shouldUseTechGunsProjectile()) return;
		ci.cancel();
		BlockPos blockPos = getPos();
		Rotation rotation = KusUtils.vectorToRotation(adjustedX, adjustedY, adjustedZ);
		for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
			GenericProjectile projectile = kusTweaks$createTechGunsProjectile(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, rotation.yaw(), rotation.pitch(), speedFactor, accuracy / 10);
			if (TurretHeadUtil.getAmpLevel(base) != 0) {
				this.getWorld().playSound(null, this.pos, ModSounds.amped, SoundCategory.BLOCKS,
						OMTConfig.TURRETS.turretSoundVolume, RandomUtil.random.nextFloat() + 0.5F);
			}
			this.getWorld().playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
					OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);
			getWorld().spawnEntity(projectile);
		}
	}
}
