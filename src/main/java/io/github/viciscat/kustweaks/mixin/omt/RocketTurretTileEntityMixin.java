package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.viciscat.kustweaks.MixinFields;
import io.github.viciscat.kustweaks.TGTurretSource;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import io.github.viciscat.kustweaks.mixin.tg.GenericGunAccessor;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.tileentity.turrets.RocketTurretTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.TGItems;
import techguns.TGuns;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.GuidedMissileProjectile;

@Mixin(value = RocketTurretTileEntity.class, remap = false)
public abstract class RocketTurretTileEntityMixin extends ProjectileTurretMixin{

	@ModifyReturnValue(method = "getAmmo", at = @At("RETURN"))
	private ItemStack getAmmo(ItemStack stack) {
		return TGItems.ROCKET;
	}

	@Override
	public GenericProjectile kusTweaks$createTechGunsProjectile(double x, double y, double z, float yaw, float pitch, float speed, float inaccuracy) {
		GenericGunAccessor rocket = (GenericGunAccessor) TGuns.rocketlauncher;
		MixinFields.projectileOverride = new MixinFields.TProjectile(x, y, z, yaw, pitch, kusTweaks$getDamage(), rocket.getSpeed(), rocket.getTTL(), inaccuracy, rocket.getDamageDropStart(), rocket.getDamageDropEnd(), rocket.getDamageMin(), rocket.getPenetration(), false, null);
		GuidedMissileProjectile projectile = new GuidedMissileProjectile(getWorld(), new EntityArmorStand(getWorld()), 0, 0, 0, 0, 0, 0, 0, 0, false, null, rocket.getRadius(), target);
		ExtendedGenericProjectile.of(projectile).kusTweaks$setOwnerTurret(new TGTurretSource((TurretHead) (Object) this));
		return projectile;
	}

	@Override
	public boolean kusTweaks$shouldUseTechGunsProjectile() {
		return true;
	}
}
