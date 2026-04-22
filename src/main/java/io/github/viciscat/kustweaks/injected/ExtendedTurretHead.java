package io.github.viciscat.kustweaks.injected;

import io.github.viciscat.kustweaks.entity.EntityTurretHeadHitbox;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

import javax.annotation.Nullable;

public interface ExtendedTurretHead {

	void kusTweaks$setHitbox(EntityTurretHeadHitbox hitbox);

	@Nullable EntityTurretHeadHitbox kusTweaks$getHitbox();

	int kusTweaks$getBulwarkUpgradeCount();

	static ExtendedTurretHead of(TurretHead turretHead) {
		return (ExtendedTurretHead) turretHead;
	}
}
