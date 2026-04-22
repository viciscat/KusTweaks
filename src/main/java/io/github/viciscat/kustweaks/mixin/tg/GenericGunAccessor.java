package io.github.viciscat.kustweaks.mixin.tg;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import techguns.items.guns.GenericGun;

@Mixin(value = GenericGun.class, remap = false)
public interface GenericGunAccessor {

	@Accessor(value = "speed")
	float getSpeed();

	@Accessor("damageDropStart")
	float getDamageDropStart();

	@Accessor("damageDropEnd")
	float getDamageDropEnd();

	@Accessor("damageMin")
	float getDamageMin();

	@Accessor("penetration")
	float getPenetration();

	@Invoker("getScaledTTL")
	int getTTL();

	@Accessor("gravity")
	double getGravity();

	@Accessor("radius")
	float getRadius();
}
