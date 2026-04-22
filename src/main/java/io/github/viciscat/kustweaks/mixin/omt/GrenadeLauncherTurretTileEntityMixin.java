package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.viciscat.kustweaks.TGTurretSource;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import io.github.viciscat.kustweaks.mixin.tg.GenericGunAccessor;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.TGItems;
import techguns.TGuns;
import techguns.entities.projectiles.GenericProjectile;
import techguns.entities.projectiles.Grenade40mmProjectile;

@Mixin(value = GrenadeLauncherTurretTileEntity.class, remap = false)
public abstract class GrenadeLauncherTurretTileEntityMixin extends ProjectileTurretMixin {

	@ModifyReturnValue(method = "getProjectileGravity", at = @At("RETURN"))
	private float getGravity(float original) {
		return (float) ((GenericGunAccessor) TGuns.grenadelauncher).getGravity();
	}

	@ModifyReturnValue(method = "getAmmo", at = @At("RETURN"))
	private ItemStack getAmmo(ItemStack original) {
		return TGItems.GRENADE_40MM;
	}

	@Override
	public GenericProjectile kusTweaks$createTechGunsProjectile(double x, double y, double z, float yaw, float pitch, float speed, float inaccuracy) {
		GenericGunAccessor pistol = (GenericGunAccessor) TGuns.grenadelauncher;
		Grenade40mmProjectile projectile = new Grenade40mmProjectile(getWorld(), x, y, z, yaw, pitch, kusTweaks$getDamage(), speed / 3, pistol.getTTL(), inaccuracy, pistol.getDamageDropStart(), pistol.getDamageDropEnd(), pistol.getDamageMin(), pistol.getPenetration(), false, null, pistol.getGravity(), pistol.getRadius(), 2);
		ExtendedGenericProjectile.of(projectile).kusTweaks$setOwnerTurret(new TGTurretSource((TurretHead) (Object) this));
		return projectile;
	}

	@Override
	public boolean kusTweaks$shouldUseTechGunsProjectile() {
		return true;
	}
}
