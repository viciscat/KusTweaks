package io.github.viciscat.kustweaks.mixin.tg;

import com.llamalad7.mixinextras.sugar.Local;
import com.srpcotesia.init.SRPCAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.damagesystem.TGDamageSource;
import techguns.entities.projectiles.DeatomizerProjectile;
import techguns.entities.projectiles.GenericProjectile;

@Mixin(value = GenericProjectile.class, remap = false)
public class DeatomizerProjectileMixin {

	@Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 1, remap = true))
	private void dealMiniDamage(RayTraceResult raytraceResultIn, CallbackInfo ci, @Local(ordinal = 0) TGDamageSource damageSource) {
		if ((Object) this instanceof DeatomizerProjectile) {
			EntityLivingBase entityHit = (EntityLivingBase) raytraceResultIn.entityHit;
			SRPCAttributes.trueDamage(null, entityHit, damageSource, 3);
		}
	}
}
