package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mcheli.weapon.MCH_EntityBaseBullet;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.api.damagesystem.DamageType;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils;

@Mixin(MCH_EntityBaseBullet.class)
public class EntityBaseBulletMixin {

	@WrapOperation(method = "onImpactEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"))
	private boolean yea(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
		return original.call(instance, new TGDamageSource("kus_heavy_ammo", source.getImmediateSource(), source.getTrueSource(), DamageType.PROJECTILE, EntityDeathUtils.DeathType.DEFAULT), amount);
	}
}
