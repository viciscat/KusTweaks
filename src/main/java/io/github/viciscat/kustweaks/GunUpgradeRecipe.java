package io.github.viciscat.kustweaks;

import io.github.viciscat.kustweaks.capability.UpgradableGun;
import io.github.viciscat.kustweaks.item.ItemGunUpgrade;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import techguns.tileentities.operation.UpgradeBenchRecipes;

public class GunUpgradeRecipe extends UpgradeBenchRecipes.UpgradeBenchRecipe {
	public GunUpgradeRecipe() {
		super(ItemStack.EMPTY, Enchantment.getEnchantmentByID(0), 0); // placeholder values
	}

	@Override
	public boolean matches(ItemStack input, ItemStack upgrade) {
		UpgradableGun gun = UpgradableGun.get(input);
		return gun != null && upgrade.getItem() instanceof ItemGunUpgrade;
	}

	public GunUpgrade getUpgrade(ItemStack upgradeItem) {
		return upgradeItem.getItem() instanceof ItemGunUpgrade upgrade ? upgrade.gunUpgrade : null;
	}
}
