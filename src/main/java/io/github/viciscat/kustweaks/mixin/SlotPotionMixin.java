package io.github.viciscat.kustweaks.mixin;

import c4.conarm.common.inventory.SlotPotion;
import cofh.thermalinnovation.item.ItemInjector;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SlotPotion.class, remap = false)
public class SlotPotionMixin {
	@Expression("? instanceof ?")
	@ModifyExpressionValue(method = "isItemValid", remap = true, at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean isItemValid(boolean original, @Local(argsOnly = true) ItemStack itemStack) {
		return original || itemStack.getItem() instanceof ItemInjector;
	}
}
