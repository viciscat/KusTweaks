package io.github.viciscat.kustweaks.mixin.tg;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.GunUpgrade;
import io.github.viciscat.kustweaks.capability.UpgradableGun;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.items.guns.GenericGun;
import techguns.recipes.AmmoSwitchRecipeFactory;

@Mixin(value = AmmoSwitchRecipeFactory.AmmoSwitchRecipe.class, remap = false)
public class AmmoSwitchRecipeMixin {
	@WrapOperation(
			method = "getCraftingResult",
			at = @At(value = "INVOKE", target = "Ltechguns/items/guns/GenericGun;getClipsize()I", remap = false),
			remap = true
	)
	public int getClipSize(GenericGun instance, Operation<Integer> original, @Local(ordinal = 0) ItemStack stack) {
		int upgradeStack = UpgradableGun.getOrEmpty(stack).getUpgradeStack(GunUpgrade.AMMO);
		int i = original.call(instance);
		return i + Math.max((int) (i * 0.1f), 1) * upgradeStack;
	}
}
