package io.github.viciscat.kustweaks.mixin.tg;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.GunUpgrade;
import io.github.viciscat.kustweaks.GunUpgradeRecipe;
import io.github.viciscat.kustweaks.KusTweaksMod;
import io.github.viciscat.kustweaks.capability.UpgradableGun;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.gui.containers.UpgradeBenchContainer;
import techguns.tileentities.operation.UpgradeBenchRecipes;

@Mixin(value = UpgradeBenchContainer.class, remap = false)
public class UpgradeBenchContainerMixin {

	@Shadow
	@Final
	protected IInventory inputSlots;

	@Shadow
	@Final
	public IInventory outputSlot;

	@Inject(
			method = "updateOutput",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEnchantments(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;",
					remap = true),
			cancellable = true)
	private void updateOutput(CallbackInfo ci, @Local ItemStack output, @Local UpgradeBenchRecipes.UpgradeBenchRecipe recipe) {
		if (recipe instanceof GunUpgradeRecipe upgradeRecipe) ci.cancel(); else return;
		GunUpgrade upgrade = upgradeRecipe.getUpgrade(inputSlots.getStackInSlot(1));
		UpgradableGun gun = UpgradableGun.get(output);
		if (gun == null || upgrade == null) {
			KusTweaksMod.LOGGER.warn("Item changed?");
			outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
			return;
		}
		if (gun.addUpgrade(upgrade)) outputSlot.setInventorySlotContents(0, output);
		else outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
	}
}
