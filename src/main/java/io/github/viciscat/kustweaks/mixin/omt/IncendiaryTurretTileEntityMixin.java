package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.viciscat.kustweaks.TGTurretSource;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import io.github.viciscat.kustweaks.mixin.tg.GenericGunAccessor;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.tileentity.turrets.IncendiaryTurretTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import techguns.TGItems;
import techguns.TGuns;
import techguns.entities.projectiles.FlamethrowerProjectile;
import techguns.entities.projectiles.GenericProjectile;

@Mixin(value = IncendiaryTurretTileEntity.class, remap = false)
public abstract class IncendiaryTurretTileEntityMixin extends ProjectileTurretMixin {

	@Unique
	private int kusTweaks$shotsLeft = 0;

	@Unique
	private ItemStack kusTweaks$ammoCache = ItemStack.EMPTY;

	@Override
	protected ItemStack getAmmoStack() {
		if (kusTweaks$ammoCache.isEmpty() || kusTweaks$shotsLeft <= 0) {
			kusTweaks$ammoCache = super.getAmmoStack();
			kusTweaks$shotsLeft = 100;
		}
		kusTweaks$shotsLeft--;
		return kusTweaks$ammoCache;
	}

	@Override
	public int getTurretBaseFireRate() {
		return 1;
	}

	@Override
	public boolean kusTweaks$shouldUseTechGunsProjectile() {
		return true;
	}

	@Override
	public GenericProjectile kusTweaks$createTechGunsProjectile(double x, double y, double z, float yaw, float pitch, float speed, float inaccuracy) {
		GenericGunAccessor flameThrower = (GenericGunAccessor) TGuns.flamethrower;
		GenericProjectile projectile = new FlamethrowerProjectile(getWorld(), x, y, z, yaw, pitch, kusTweaks$getDamage(), speed / 2, flameThrower.getTTL(), inaccuracy, flameThrower.getDamageDropStart(), flameThrower.getDamageDropEnd(), flameThrower.getDamageMin(), flameThrower.getPenetration(), false, null, flameThrower.getGravity());
		ExtendedGenericProjectile.of(projectile).kusTweaks$setOwnerTurret(new TGTurretSource((TurretHead) (Object) this));
		return projectile;
	}

	@ModifyReturnValue(method = "getAmmo", at = @At("RETURN"))
	private ItemStack getAmmo(ItemStack stack) {
		return TGItems.FUEL_TANK;
	}
}
