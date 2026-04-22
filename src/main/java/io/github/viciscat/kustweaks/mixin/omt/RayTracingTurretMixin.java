package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.viciscat.kustweaks.MixinFields;
import io.github.viciscat.kustweaks.TGTurretSource;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import io.github.viciscat.kustweaks.mixin.tg.GenericGunAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import omtteam.openmodularturrets.tileentity.turrets.LaserTurretTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.RailGunTurretTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.RayTracingTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.TGuns;
import techguns.entities.projectiles.GaussProjectile;
import techguns.entities.projectiles.LaserProjectile;

import java.util.List;

@Mixin(value = RayTracingTurret.class, remap = false)
public abstract class RayTracingTurretMixin extends AbstractDirectedTurretMixin {


	@SuppressWarnings("DataFlowIssue")
	@WrapOperation(method = "shootRay", at = @At(value = "INVOKE", target = "Lomtteam/omlib/util/WorldUtil;traceEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/World;)Ljava/util/List;"))
	private List<RayTraceResult> onShoot(Entity aabb, Vec3d pos, Vec3d targetPos, World toIgnore, Operation<List<RayTraceResult>> original, @Local(name = "hit") LocalBooleanRef hit, @Local(name = "accuracy") double accuracy) {
		Object _this = this;
		Vec3d direction = targetPos.subtract(pos);
		double d3 = MathHelper.sqrt(direction.x * direction.x + direction.z * direction.z);
		float yaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(direction.z, direction.x) * (180D / Math.PI)) - 90);
		float pitch = MathHelper.wrapDegrees((float) (-MathHelper.atan2(direction.y, d3) * (180D / Math.PI)));
		float spread = (float) accuracy / 200;
		switch (_this) {
			case LaserTurretTileEntity ignored1 -> {
				GenericGunAccessor lasergun = (GenericGunAccessor) TGuns.lasergun;
				MixinFields.laserProjectSource = new TGTurretSource((TurretHead) (Object) this);
				LaserProjectile projectile = new LaserProjectile(getWorld(), pos.x, pos.y, pos.z, yaw, pitch, kusTweaks$getDamage(), lasergun.getSpeed(), 7, spread, lasergun.getDamageDropStart(), lasergun.getDamageDropEnd(), lasergun.getDamageMin(), lasergun.getPenetration(), false, null);
				getWorld().spawnEntity(projectile);
			}

			case RailGunTurretTileEntity ignored -> {
				GenericGunAccessor gauss = (GenericGunAccessor) TGuns.gaussrifle;
				MixinFields.projectileOverride = new MixinFields.TProjectile(pos.x, pos.y, pos.z, yaw, pitch, kusTweaks$getDamage(), gauss.getSpeed(), gauss.getTTL(), spread, gauss.getDamageDropStart(), gauss.getDamageDropEnd(), gauss.getDamageMin(), gauss.getPenetration(), false, null);
				GaussProjectile projectile = new GaussProjectile(getWorld(), new EntityArmorStand(getWorld()), 0, 0, 0, 0, 0, 0, 0, 0, false, null);
				ExtendedGenericProjectile.of(projectile).kusTweaks$setOwnerTurret(new TGTurretSource((TurretHead) (Object) this));
				getWorld().spawnEntity(projectile);
			}

			default -> {
				return original.call(aabb, pos, targetPos, toIgnore);
			}
		}
		hit.set(true);

		return List.of();
	}
}
