package io.github.viciscat.kustweaks.item;

import io.github.viciscat.kustweaks.GunUpgrade;
import net.minecraft.item.Item;

public class ItemGunUpgrade extends Item {

	public final GunUpgrade gunUpgrade;

	public ItemGunUpgrade(GunUpgrade gunUpgrade) {
		this.gunUpgrade = gunUpgrade;
		setRegistryName(gunUpgrade.id() + "_gun_upgrade");
	}

}
