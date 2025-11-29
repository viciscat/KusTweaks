package io.github.viciscat.kustweaks.mixin.tg;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.GunUpgrade;
import io.github.viciscat.kustweaks.KusAttributes;
import io.github.viciscat.kustweaks.capability.UpgradableGun;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import io.github.viciscat.kustweaks.injected.ExtendedWorld;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
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

import java.util.Set;

@Mixin(GenericProjectile.class)
public abstract class GenericProjectileMixin extends Entity implements ExtendedGenericProjectile {

    @Shadow(remap = false) protected EntityLivingBase shooter;
    @Shadow(remap = false) protected int ticksToLive;
    @Shadow(remap = false) protected int lifetime;
	@Shadow(remap = false)
	float damage;
	@Shadow(remap = false)
	float damageMin;
    @Unique
    protected int kusTweaks$bouncesLeft = 0;
	@Unique
	protected int kusTweaks$pierceLeft = 0;
	@Unique
	protected Set<Object> kusTweaks$piercedObjects = ObjectSets.emptySet();
	@Unique
	protected double kusTweaks$explosionSizeMult = 1;

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
			UpgradableGun gun = UpgradableGun.get(shooter.getHeldItemMainhand());
			if (gun == null) gun = UpgradableGun.getOrEmpty(shooter.getHeldItemOffhand());
			kusTweaks$bouncesLeft = mult + gun.getUpgradeStack(GunUpgrade.BOUNCE);
			float damageMult = 1 + gun.getUpgradeStack(GunUpgrade.DAMAGE) * 0.1f;
			damage *= damageMult;
			damageMin *= damageMult;
			kusTweaks$explosionSizeMult *= KusAttributes.getAttributeOrDefault(shooter, KusAttributes.EXPLOSION_SIZE_MULTIPLIER);
			kusTweaks$explosionSizeMult *= (1 + gun.getUpgradeStack(GunUpgrade.EXPLOSION_SIZE) * 0.1);

			kusTweaks$pierceLeft = gun.getUpgradeStack(GunUpgrade.PIERCE);
			kusTweaks$piercedObjects = new ObjectOpenHashSet<>(kusTweaks$pierceLeft);
		}
    }

	@Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;"))
	private void doNotRaycastHitBlocks(CallbackInfo ci) {
		ExtendedWorld.ignore(world, kusTweaks$piercedObjects::contains);
	}

	@WrapOperation(
			method = "onHit", remap = false,
			at = @At(value = "INVOKE", target = "Ltechguns/entities/projectiles/GenericProjectile;setDead()V", remap = true, ordinal = 0),
			slice = @Slice(from = @At(value = "INVOKE", target = "Ltechguns/entities/projectiles/GenericProjectile;getProjectileDamageSource()Ltechguns/damagesystem/TGDamageSource;", remap = false))
	)
	private void performEntityPierce(GenericProjectile instance, Operation<Void> original, @Local(argsOnly = true) RayTraceResult rayTraceResult) {
		Object _this = this;
		//noinspection ConstantValue
		if (_this instanceof AbstractBeamProjectile || _this instanceof RocketProjectileNuke) return;
		Entity hit = rayTraceResult.entityHit;
		if (hit == null) {
			original.call(instance);
			return;
		}
		if (kusTweaks$pierceLeft > 0) {
			kusTweaks$pierceLeft--;
			kusTweaks$piercedObjects.add(hit);
			posX = rayTraceResult.hitVec.x - motionX;
			posY = rayTraceResult.hitVec.y - motionY;
			posZ = rayTraceResult.hitVec.z - motionZ;
		} else original.call(instance);

	}

	@Inject(method = "onHit", remap = false, at = @At("HEAD"), cancellable = true)
	private void doNotHitPiercedThingsAgain(RayTraceResult raytraceResultIn, CallbackInfo ci) {
		if (kusTweaks$piercedObjects.contains(raytraceResultIn.entityHit) || kusTweaks$piercedObjects.contains(raytraceResultIn.getBlockPos())) ci.cancel();
	}

    @WrapOperation(
            method = "onHit", remap = false,
            at = @At(value = "INVOKE", target = "Ltechguns/entities/projectiles/GenericProjectile;setDead()V", remap = true),
            slice = @Slice(from = @At(value = "INVOKE", target = "Ltechguns/entities/projectiles/GenericProjectile;hitBlock(Lnet/minecraft/util/math/RayTraceResult;)V", remap = false))
    )
    private void performBounceAndBlockPierce(GenericProjectile instance, Operation<Void> original, @Local(argsOnly = true) RayTraceResult rayTraceResult) {
        Object _this = this;
        //noinspection ConstantValue
        if (_this instanceof AbstractBeamProjectile || _this instanceof RocketProjectileNuke) return;
		if (kusTweaks$pierceLeft > 0) {
			float resistance = instance.world.getBlockState(rayTraceResult.getBlockPos()).getBlock().getExplosionResistance(instance);
			int pierceCost;
			if (resistance >= 3200) pierceCost = 4;
			else if (resistance >= 1200) pierceCost = 2;
			else pierceCost = 1;
			if (kusTweaks$pierceLeft >= pierceCost) {
				kusTweaks$pierceLeft -= pierceCost;
				if (resistance <= 0.3f) instance.world.destroyBlock(rayTraceResult.getBlockPos(), false);
				kusTweaks$piercedObjects.add(rayTraceResult.getBlockPos());
				posX = rayTraceResult.hitVec.x - motionX;
				posY = rayTraceResult.hitVec.y - motionY;
				posZ = rayTraceResult.hitVec.z - motionZ;
				return;
			}
		}
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

	@Override
	public double kusTweaks$explosionSizeMult() {
		return kusTweaks$explosionSizeMult;
	}

	@Inject(method = "writeEntityToNBT", at = @At("TAIL"))
	private void writeEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
		compound.setDouble("size_multiplier", kusTweaks$explosionSizeMult);
	}

	@Inject(method = "readEntityFromNBT", at = @At("TAIL"))
	private void readEntityFromNBT(NBTTagCompound compound, CallbackInfo ci) {
		if (compound.hasKey("size_multiplier")) kusTweaks$explosionSizeMult = compound.getDouble("size_multiplier");
	}
}
