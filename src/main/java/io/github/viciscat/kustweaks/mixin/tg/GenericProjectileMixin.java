package io.github.viciscat.kustweaks.mixin.tg;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.KusAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.entities.projectiles.AbstractBeamProjectile;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.RocketProjectileNuke;

@Mixin(GenericProjectile.class)
public abstract class GenericProjectileMixin extends Entity {

    @Shadow(remap = false) protected EntityLivingBase shooter;
    @Shadow(remap = false) protected int ticksToLive;
    @Shadow(remap = false) protected int lifetime;
    @Unique
    protected int kusTweaks$bouncesLeft = 0;

    public GenericProjectileMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "initProjectile", at = @At("TAIL"), remap = false)
    private void initProjectile(CallbackInfo ci) {
        if (world.isRemote) kusTweaks$bouncesLeft = 0;
        else if (this.shooter != null) {
            double attr = KusAttributes.getAttributeOrDefault(shooter, KusAttributes.TECHGUNS_PROJECTILE_BOUNCE_CHANCE);
            int mult = (int) attr;
            if (Math.random() < (attr % 1)) mult++;
            kusTweaks$bouncesLeft = mult;
        }
    }

    @WrapOperation(
            method = "onHit", remap = false,
            at = @At(value = "INVOKE", target = "Ltechguns/entities/projectiles/GenericProjectile;setDead()V", remap = true),
            slice = @Slice(from = @At(value = "INVOKE", target = "Ltechguns/entities/projectiles/GenericProjectile;hitBlock(Lnet/minecraft/util/math/RayTraceResult;)V", remap = false))
    )
    private void performProjectileBounce(GenericProjectile instance, Operation<Void> original, @Local(argsOnly = true) RayTraceResult rayTraceResult) {
        Object _this = this;
        //noinspection ConstantValue
        if (_this instanceof AbstractBeamProjectile || _this instanceof RocketProjectileNuke) return;
        if (kusTweaks$bouncesLeft <= 0) {
            original.call(instance);
            return;
        }
        kusTweaks$bouncesLeft--;
        Vec3d vec3d = new Vec3d(motionX, motionY, motionZ);
        Vec3d normal = new Vec3d(rayTraceResult.sideHit.getDirectionVec()).normalize();
        Vec3d res = vec3d.subtract(normal.scale(2*vec3d.dotProduct(normal))).normalize().scale(vec3d.length());
        motionX = res.x;
        motionY = res.y;
        motionZ = res.z;
        ticksToLive = lifetime;

        isDead = false;

    }
}
