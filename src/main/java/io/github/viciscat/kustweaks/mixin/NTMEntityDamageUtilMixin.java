package io.github.viciscat.kustweaks.mixin;

import com.hbm.util.EntityDamageUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityDamageUtil.class, remap = false)
public class NTMEntityDamageUtilMixin {
	@Inject(method = "damageEntityNT", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;setAbsorptionAmount(F)V", ordinal = 0, shift = At.Shift.AFTER, remap = true))
	private static void callDamageEvent(CallbackInfo ci, @Local(argsOnly = true) DamageSource source, @Local(argsOnly = true) LocalFloatRef damageAmount, @Local(argsOnly = true) EntityLivingBase base) {
		damageAmount.set(ForgeHooks.onLivingDamage(base, source, damageAmount.get()));
	}

	@WrapOperation(method = "attackEntityFromNT", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", remap = true))
	private static boolean dontDoAZeroAttack(EntityLivingBase instance, DamageSource source, float amount, Operation<Boolean> original) {
		return false;
	}

}
