package io.github.viciscat.kustweaks.mixin.tg;

import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.damagesystem.TGExplosion;

@Mixin(TGExplosion.class)
public class TGExplosionMixin {
    @Shadow(remap = false)
    double primaryRadius;

    @Shadow(remap = false)
    double secondaryRadius;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void increaseRadius(World world, Entity exploder, Entity projectile, double x, double y, double z, double primaryDamage, double secondaryDamage, double primaryRadius, double secondaryRadius, double blockDamageFactor, CallbackInfo ci) {
        if (exploder instanceof EntityLivingBase) {
			double radiusMultiplier = 1;
			if (projectile instanceof ExtendedGenericProjectile extendedGenericProjectile) radiusMultiplier *= extendedGenericProjectile.kusTweaks$explosionSizeMult();
            this.primaryRadius *= radiusMultiplier;
            this.secondaryRadius *= radiusMultiplier;
        }
    }
}
