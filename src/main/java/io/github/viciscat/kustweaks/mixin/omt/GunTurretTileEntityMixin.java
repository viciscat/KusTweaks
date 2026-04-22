package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.viciscat.kustweaks.TGTurretSource;
import io.github.viciscat.kustweaks.injected.ExtendedGenericProjectile;
import io.github.viciscat.kustweaks.mixin.tg.GenericGunAccessor;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.tileentity.turrets.GunTurretTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.TGItems;
import techguns.TGuns;
import techguns.entities.projectiles.GenericProjectile;

@Mixin(value = GunTurretTileEntity.class, remap = false)
public abstract class GunTurretTileEntityMixin extends ProjectileTurretMixin {
	@ModifyReturnValue(method = "getAmmo", at = @At("RETURN"))
	private ItemStack getAmmo(ItemStack stack) {
		return TGItems.PISTOL_ROUNDS;
	}

	@Override
	public GenericProjectile kusTweaks$createTechGunsProjectile(double x, double y, double z, float yaw, float pitch, float speed, float inaccuracy) {
		GenericGunAccessor pistol = (GenericGunAccessor) TGuns.pistol;
		GenericProjectile projectile = new GenericProjectile(getWorld(), x, y, z, yaw, pitch, kusTweaks$getDamage(), speed, pistol.getTTL(), inaccuracy, pistol.getDamageDropStart(), pistol.getDamageDropEnd(), pistol.getDamageMin(), pistol.getPenetration(), false, null);
		ExtendedGenericProjectile.of(projectile).kusTweaks$setOwnerTurret(new TGTurretSource((TurretHead) (Object) this));
		return projectile;
	}

	@Override
	public boolean kusTweaks$shouldUseTechGunsProjectile() {
		return true;
	}
}
