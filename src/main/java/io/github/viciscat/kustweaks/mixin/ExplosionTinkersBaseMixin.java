package io.github.viciscat.kustweaks.mixin;

import io.github.viciscat.kustweaks.KusAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.capability.projectile.TinkerProjectileHandler;
import uvmidnight.totaltinkers.explosives.ExplosionTinkersBase;

@Mixin(ExplosionTinkersBase.class)
public class ExplosionTinkersBaseMixin {

    @Mutable
    @Shadow(remap = false) @Final private float size;

    @Inject(
            method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;DDDFLslimeknights/tconstruct/library/capability/projectile/TinkerProjectileHandler;Lnet/minecraft/entity/EntityLivingBase;)V",
            at = @At("TAIL")
    )
    private void increaseSize(World worldIn, Entity entityIn, double x, double y, double z, float size, TinkerProjectileHandler tinkerProjectile, EntityLivingBase attacker, CallbackInfo ci) {
        if (attacker != null) {
            this.size *= (float) KusAttributes.getAttributeOrDefault(attacker, KusAttributes.EXPLOSION_SIZE_MULTIPLIER);
        }
    }
}
