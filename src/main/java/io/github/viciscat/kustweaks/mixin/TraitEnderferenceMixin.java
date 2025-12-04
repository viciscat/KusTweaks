package io.github.viciscat.kustweaks.mixin;

import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.tools.traits.TraitEnderference;

@Mixin(value = TraitEnderference.class, remap = false)
public class TraitEnderferenceMixin {

	@Expression("? instanceof ?")
	@ModifyExpressionValue(method = "onHit", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean onHit(boolean original, @Local(argsOnly = true, ordinal = 1) EntityLivingBase target) {
		return original || target instanceof EntityFerEnderman || target instanceof EntityInfEnderman;
	}
}
