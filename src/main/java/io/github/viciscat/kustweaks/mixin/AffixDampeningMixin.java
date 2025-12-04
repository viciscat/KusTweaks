package io.github.viciscat.kustweaks.mixin;

import c4.champions.common.affix.affix.AffixDampening;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AffixDampening.class, remap = false)
public class AffixDampeningMixin {

	@Expression("? instanceof ?")
	@ModifyExpressionValue(method = "onHurt", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean checkGun(boolean original, @Local(argsOnly = true) DamageSource source) {
		return original || source.getDamageType().equals("tg_bullet") || source.getDamageType().equals("kus_heavy_ammo");
	}
}
