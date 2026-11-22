package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import creepu.EntityNetheru;
import io.github.viciscat.kustweaks.MixinMethods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityNetheru.class)
public class EntityNetheruMixin extends EntityCreeper {

	public EntityNetheruMixin(World worldIn) {
		super(worldIn);
	}

	@WrapOperation(method = "explodeB", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZ)Lnet/minecraft/world/Explosion;", remap = true), remap = false)
	private Explosion biggerExplosion(World instance, Entity entityIn, double x, double y, double z, float strength, boolean damagesTerrain, Operation<Explosion> original) {
		float mult = MixinMethods.getExplosionMultiplier(entityIn);
		if (mult >= 5) {
			MixinMethods.bigBoomAt((EntityNetheru) (Object) this, (getPowered() ? 55 : 27.5f) * mult/5);
			return new Explosion(entityIn.getEntityWorld(), entityIn, x, y, z, 0, false, damagesTerrain);
		}
		return original.call(instance, entityIn, x, y, z, strength * mult, damagesTerrain);
	}

	@ModifyArg(method = "explodeB", at = @At(value = "INVOKE", target = "Lcreepu/Explosion2;<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;DDDFZZ)V"), remap = false, index = 5)
	private float biggerExplosion2(float size) {
    	EntityNetheru thiz = (EntityNetheru) (Object) this;
		return size * MixinMethods.getExplosionMultiplier(thiz);
	}
}
