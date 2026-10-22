package io.github.viciscat.kustweaks.item;

import io.github.viciscat.kustweaks.GunUpgrade;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemGunUpgrade extends Item {

	public final GunUpgrade gunUpgrade;

	public ItemGunUpgrade(GunUpgrade gunUpgrade) {
		this.gunUpgrade = gunUpgrade;
		setRegistryName(new ResourceLocation(gunUpgrade.id().getNamespace(), gunUpgrade.id().getPath() + "_gun_upgrade"));
	}

}
