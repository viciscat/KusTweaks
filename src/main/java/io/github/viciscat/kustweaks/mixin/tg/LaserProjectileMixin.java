package io.github.viciscat.kustweaks.mixin.tg;

import io.github.viciscat.kustweaks.MixinFields;
import io.github.viciscat.kustweaks.injected.ExtendedTurretHead;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.entities.projectiles.LaserProjectile;

@Mixin(value = LaserProjectile.class, remap = false)
public abstract class LaserProjectileMixin extends GenericProjectileMixin {

	public LaserProjectileMixin(World worldIn) {
		super(worldIn);
	}

	@Inject(method = {
			"<init>(Lnet/minecraft/world/World;DDDFFFFIFFFFFZLtechguns/entities/projectiles/EnumBulletFirePos;)V",
			"<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;FFIFFFFFZLtechguns/entities/projectiles/EnumBulletFirePos;)V"
	},
	at = @At(value = "INVOKE", target = "Ltechguns/entities/projectiles/LaserProjectile;trace()V"))
	private void injectTurretSource(CallbackInfo ci) {
		if (MixinFields.laserProjectSource != null) {
			kusTweaks$ownerTurret = MixinFields.laserProjectSource;
			shooter = ExtendedTurretHead.of(kusTweaks$ownerTurret.head()).kusTweaks$getHitbox();
			MixinFields.laserProjectSource = null;
		}
	}
}
