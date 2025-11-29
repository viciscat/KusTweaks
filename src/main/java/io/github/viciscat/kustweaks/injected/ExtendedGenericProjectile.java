package io.github.viciscat.kustweaks.injected;

import techguns.entities.projectiles.GenericProjectile;

public interface ExtendedGenericProjectile {
	double kusTweaks$explosionSizeMult();

	static ExtendedGenericProjectile of(GenericProjectile projectile) {
		return (ExtendedGenericProjectile) projectile;
	}
}
