package io.github.viciscat.kustweaks.injected;

import techguns.entities.projectiles.GenericProjectile;

public interface ExtendedProjectileTurret {

	default GenericProjectile kusTweaks$createTechGunsProjectile(double x, double y, double z, float yaw, float pitch, float speed, float inaccuracy) {
		return new GenericProjectile(null);
	}

	default boolean kusTweaks$shouldUseTechGunsProjectile() {
		return false;
	}
}
