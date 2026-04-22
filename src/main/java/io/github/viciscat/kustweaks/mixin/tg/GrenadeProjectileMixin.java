package io.github.viciscat.kustweaks.mixin.tg;

import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.entities.projectiles.GrenadeProjectile;

@Mixin(value = GrenadeProjectile.class, remap = false)
public abstract class GrenadeProjectileMixin extends GenericProjectileMixin {

	public GrenadeProjectileMixin(World worldIn) {
		super(worldIn);
	}

	@Inject(method = "<init>(Ltechguns/entities/projectiles/GrenadeProjectile;DDD)V", at = @At("TAIL"))
	private void copySourceTurret(GrenadeProjectile other, double x, double y, double z, CallbackInfo ci) {
		kusTweaks$ownerTurret = ExtendedGenericProjectile.of(other).kusTweaks$getOwnerTurret();
	}
}
