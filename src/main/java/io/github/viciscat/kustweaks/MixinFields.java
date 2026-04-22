package io.github.viciscat.kustweaks;

import techguns.entities.projectiles.EnumBulletFirePos;

import javax.annotation.Nullable;

/**
 * Static fields that cannot be in mixins directly because they need to be edited somewhere else
 */
public class MixinFields {

	public static @Nullable TGTurretSource laserProjectSource = null;
	public static @Nullable TProjectile projectileOverride = null;

	public record TProjectile(double posX, double posY, double posZ, float yaw, float pitch, float damage,
							  float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
							  boolean blockdamage, EnumBulletFirePos firePos) {}
}
