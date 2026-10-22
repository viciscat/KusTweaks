package io.github.viciscat.kustweaks.mixin.tg;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.GunUpgrades;
import io.github.viciscat.kustweaks.KusAttributes;
import io.github.viciscat.kustweaks.MixinFields;
import io.github.viciscat.kustweaks.TGTurretSource;
import io.github.viciscat.kustweaks.capability.UpgradableGun;
import io.github.viciscat.kustweaks.entity.EntityTurretHeadHitbox;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import io.github.viciscat.kustweaks.injected.ExtendedTurretHead;
import io.github.viciscat.kustweaks.injected.ExtendedWorld;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.util.OMTUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.TGConfig;
import techguns.entities.projectiles.*;

import javax.annotation.Nullable;
import java.util.Set;


@Mixin(GenericProjectile.class)
public abstract class GenericProjectileMixin extends Entity implements ExtendedGenericProjectile {

	@Shadow(remap = false)
	protected EntityLivingBase shooter;
	@Shadow(remap = false)
	protected int ticksToLive;
	@Shadow(remap = false)
	protected int lifetime;
	@Shadow(remap = false)
	float damage;
	@Shadow(remap = false)
	float damageMin;
	@Shadow(remap = false)
	public float speed;
	@Shadow(remap = false)
	float damageDropStart;
	@Shadow(remap = false)
	float damageDropEnd;
	@Shadow(remap = false)
	double gravity;
	@Unique
	protected int kusTweaks$bouncesLeft = 0;
	@Unique
	protected int kusTweaks$pierceLeft = 0;
	@Unique
	protected Set<Object> kusTweaks$piercedObjects = ObjectSets.emptySet();
	@Unique
	protected double kusTweaks$explosionSizeMult = 1;
	@Unique
	protected @Nullable TGTurretSource kusTweaks$ownerTurret;
	@Unique
	protected @Nullable BlockPos kusTweaks$startPosition;

	public GenericProjectileMixin(World worldIn) {
		super(worldIn);
	}

	@Override
	public void kusTweaks$setOwnerTurret(@Nullable TGTurretSource ownerTurret) {
		this.kusTweaks$ownerTurret = ownerTurret;
		shooter = ownerTurret != null ? ExtendedTurretHead.of(ownerTurret.head()).kusTweaks$getHitbox() : null;
	}

	@Override
	public TGTurretSource kusTweaks$getOwnerTurret() {
		return kusTweaks$ownerTurret;
	}

	@WrapOperation(
			method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;FFIFFFFFZLtechguns/entities/projectiles/EnumBulletFirePos;)V",
			at = @At(value = "INVOKE", target = "Ltechguns/entities/projectiles/GenericProjectile;initProjectile(Lnet/minecraft/world/World;DDDFFFFIFFFFFZLtechguns/entities/projectiles/EnumBulletFirePos;)V"),
			remap = false
	)
	private void missingConstructorWorkaround(GenericProjectile instance, World worldIn, double posX, double posY, double posZ, float yaw, float pitch, float damage,
											  float speed, int TTL, float spread, float dmgDropStart, float dmgDropEnd, float dmgMin, float penetration,
											  boolean blockdamage, EnumBulletFirePos firePos, Operation<Void> original) {
		MixinFields.TProjectile p = MixinFields.projectileOverride;
		if (p == null) original.call(instance, worldIn, posX, posY, posZ, yaw, pitch, damage, speed, TTL, spread, dmgDropStart, dmgDropEnd, dmgMin, penetration, blockdamage, firePos);
		else {
			MixinFields.projectileOverride = null;
			shooter = null;
			original.call(instance, worldIn, p.posX(), p.posY(), p.posZ(), p.yaw(), p.pitch(), p.damage(), p.speed(), p.TTL(), p.spread(), p.dmgDropStart(), p.dmgDropEnd(), p.dmgMin(), p.penetration(), p.blockdamage(), p.firePos());
		}
	}

	@Inject(method = "initStartPos", at = @At("RETURN"), remap = false)
	private void onInitStartPos(CallbackInfo ci) {
		kusTweaks$startPosition = new BlockPos(posX, posY, posZ);
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
			kusTweaks$bouncesLeft = mult + gun.getUpgradeStack(GunUpgrades.BOUNCE);
			float damageMult = 1 + gun.getUpgradeStack(GunUpgrades.DAMAGE) * 0.1f;
			damage *= damageMult;
			damageMin *= damageMult;
			kusTweaks$explosionSizeMult *= KusAttributes.getAttributeOrDefault(shooter, KusAttributes.EXPLOSION_SIZE_MULTIPLIER);
			kusTweaks$explosionSizeMult *= (1 + gun.getUpgradeStack(GunUpgrades.EXPLOSION_SIZE) * 0.1);

			int projectileSpeedStacks = gun.getUpgradeStack(GunUpgrades.PROJECTILE_SPEED);
			float projectileDistMult = 1 + projectileSpeedStacks * 0.1f;
			float projectileSpeedMult = 1 + projectileSpeedStacks * 0.05f;
			speed *= projectileSpeedMult;
			gravity /= projectileSpeedMult;
			damageDropStart *= projectileDistMult;
			damageDropEnd *= projectileDistMult;
			ticksToLive = (int) (ticksToLive * projectileDistMult);
			lifetime = (int) (lifetime * projectileDistMult);

			kusTweaks$pierceLeft = gun.getUpgradeStack(GunUpgrades.PIERCE);
			kusTweaks$piercedObjects = new ObjectOpenHashSet<>(kusTweaks$pierceLeft);
		}
	}

	@Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;"))
	private void doNotRaycastHitBlocks(CallbackInfo ci) {
		ExtendedWorld.ignore(world, blockPos -> kusTweaks$piercedObjects.contains(blockPos) ||
				(kusTweaks$ownerTurret != null && kusTweaks$ownerTurret.head().getPos().equals(blockPos)) ||
				world.isRemote && blockPos.equals(kusTweaks$startPosition) && world.getBlockState(blockPos).getBlock() instanceof BlockAbstractTurretHead // owner turret is not synced to the client, so just ignore in geral
		);
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
		Vec3d res = vec3d.subtract(normal.scale(2 * vec3d.dotProduct(normal))).normalize().scale(vec3d.length());
		motionX = res.x;
		motionY = res.y;
		motionZ = res.z;
		ticksToLive = lifetime;

		isDead = false;
	}

	// Set the start position on the client
	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);
		if (!world.isRemote || kusTweaks$startPosition != null || (x == 0 && y == 0 && z == 0)) return;
		BlockPos pos = new BlockPos(x, y, z);
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof BlockAbstractTurretHead) {
			kusTweaks$startPosition = pos;
		} else {
			kusTweaks$startPosition = BlockPos.ORIGIN;
		}
	}

	@Definition(id = "entity1", local = @Local(type = Entity.class, name = "entity1")) // shouldn't be obfsucated
	@Definition(id = "shooter", field = "Ltechguns/entities/projectiles/GenericProjectile;shooter:Lnet/minecraft/entity/EntityLivingBase;")
	@Expression("entity1 != this.shooter")
	@ModifyExpressionValue(method = "findEntityOnPath", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
	private boolean doNotHitOwnerTurretHitbox(boolean original, @Local(name = "entity1") Entity entity) {
		if (!original) return false;
		if (kusTweaks$piercedObjects.contains(entity)) return false;
		if (world.isRemote && entity instanceof EntityTurretHeadHitbox hitbox && hitbox.getPosition().equals(kusTweaks$startPosition)) return false;
		if (kusTweaks$ownerTurret == null) return true;
		if (entity instanceof EntityPlayer player && OMTUtil.canDamagePlayer(player, kusTweaks$ownerTurret.head().getBase())) return true;
		return OMTUtil.canDamageEntity(entity, kusTweaks$ownerTurret.head().getBase());
	}

	@WrapOperation(
			method = "onHit",
			at = @At(
					value = "INVOKE",
					target = "Ltechguns/damagesystem/DamageSystem;getDamageFactor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/EntityLivingBase;)F"
			),
			remap = false
	)
	private float modifyDamageFactorFromTurret(EntityLivingBase attacker, EntityLivingBase target, Operation<Float> original) {
		if (kusTweaks$ownerTurret == null) return original.call(attacker, target);
		return TGConfig.damageTurretToPlayer;
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
