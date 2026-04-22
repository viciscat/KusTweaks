package io.github.viciscat.kustweaks.mixin.omt;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.util.TurretHeadUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractDirectedTurret.class, remap = false)
public abstract class AbstractDirectedTurretMixin extends TurretHeadMixin {
	@Shadow
	protected float pitch;

	@Shadow
	protected float yaw;

	@Shadow
	protected abstract void doTargetedShot(Entity target, ItemStack ammo);

	@Shadow
	public abstract boolean forceShot();

	@Unique
	private float kusTweaks$extraShots = 0;

	@Inject(
			method = "update",
			at = @At(
					value = "INVOKE", target = "Lomtteam/openmodularturrets/tileentity/turrets/AbstractDirectedTurret;getAmmoStack()Lnet/minecraft/item/ItemStack;",
					remap = false
			),
			remap = true
	)
	private void shootExtra(CallbackInfo ci) {
		float v = this.getTurretBaseFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(base, (TurretHead) (Object) this));
		if (v < 1) kusTweaks$extraShots += Math.max(0, 1 - 1 / v);
		while (kusTweaks$extraShots >= 1) {
			kusTweaks$extraShots--;
			ItemStack ammo = getAmmoStack();
			if (ammo == ItemStack.EMPTY && requiresAmmo()) {
				continue;
			}

			//Finally, try to shoot if criteria is met.
			if (target != null) {
				doTargetedShot(this.target, ammo);
			} else if (autoFire) {
				forceShot();
			} else continue;
		}
	}
}
