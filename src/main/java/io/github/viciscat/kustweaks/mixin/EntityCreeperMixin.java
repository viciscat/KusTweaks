package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.viciscat.kustweaks.MixinMethods;
import laputan.capabilities.ISizeCapability;
import laputan.capabilities.SizeProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityCreeper.class)
public abstract class EntityCreeperMixin {

	@Shadow
	public abstract boolean getPowered();

	@WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZ)Lnet/minecraft/world/Explosion;"))
	private Explosion biggerExplosion(World instance, Entity entityIn, double x, double y, double z, float strength, boolean damagesTerrain, Operation<Explosion> original) {
		float mult = MixinMethods.getExplosionMultiplier(entityIn);
		if (mult >= 5) {
			MixinMethods.bigBoomAt((EntityCreeper) (Object) this, (this.getPowered() ? 170 : 85) * mult/5);
			return new Explosion(entityIn.getEntityWorld(), entityIn, x, y, z, 0, false, damagesTerrain);
		}
		return original.call(instance, entityIn, x, y, z, strength * mult, damagesTerrain);
	}
}
