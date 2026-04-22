package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.KusItems;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.client.gui.slot.UpgradeSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(UpgradeSlot.class)
public class UpgradeSlotMixin {

	@ModifyReturnValue(method = "isItemValid", at = @At("RETURN"))
	private boolean isValid(boolean original, @Local(argsOnly = true) ItemStack stack) {
		return original || stack.getItem() == KusItems.itemBulwarkUpgrade;
	}
}
