package io.github.viciscat.kustweaks.mixin.tg;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.GunUpgrade;
import io.github.viciscat.kustweaks.capability.UpgradableGun;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.events.TGGuiEvents;
import techguns.items.guns.GenericGun;

@Mixin(value = TGGuiEvents.class, remap = false)
public class TGGuiEventsMixin {

	@WrapOperation(method = "drawGunAmmoCount", at = @At(value = "INVOKE", target = "Ltechguns/items/guns/GenericGun;getClipsizeTooltip()I"))
	private int modifyGunAmount(GenericGun instance, Operation<Integer> original, @Local(argsOnly = true) ItemStack stack) {
		int upgradeStack = UpgradableGun.getOrEmpty(stack).getUpgradeStack(GunUpgrade.AMMO);
		Integer i = original.call(instance);
		return i + Math.max((int) (i * 0.1f), 1) * upgradeStack;
	}
}
