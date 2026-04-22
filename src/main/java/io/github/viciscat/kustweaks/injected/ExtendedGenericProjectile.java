package io.github.viciscat.kustweaks.injected;

import io.github.viciscat.kustweaks.TGTurretSource;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import techguns.entities.projectiles.GenericProjectile;

public interface ExtendedGenericProjectile {
	double kusTweaks$explosionSizeMult();

	void kusTweaks$setOwnerTurret(TGTurretSource turretSource);

	TGTurretSource kusTweaks$getOwnerTurret();

	static ExtendedGenericProjectile of(GenericProjectile projectile) {
		return (ExtendedGenericProjectile) projectile;
	}
}
