package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.entity.EntityTurretHeadHitbox;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.OMTUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.TGItems;

@Mixin(value = OMTUtil.class, remap = false)
public class OMTUtilMixin {

	@WrapMethod(method = "canDamageEntity")
	private static boolean canDamageEntity(Entity entity, TurretBase base, Operation<Boolean> original) {
		if (entity instanceof EntityTurretHeadHitbox turretHeadHitbox && !turretHeadHitbox.isConcealed() && turretHeadHitbox.getTurretHead() != null && turretHeadHitbox.getTurretHead().getBase() == base) return false;
		return original.call(entity, base);
	}

	@ModifyReturnValue(method = "isItemStackValidAmmo", at = @At("TAIL"))
	private static boolean isItemStackValidAmmo(boolean original, @Local(argsOnly = true) ItemStack stack) {
		return original || TGItems.PISTOL_ROUNDS.isItemEqual(stack) || TGItems.GAUSSRIFLE_SLUGS.isItemEqual(stack) || TGItems.GRENADE_40MM.isItemEqual(stack) || TGItems.ROCKET.isItemEqual(stack) || TGItems.FUEL_TANK.isItemEqual(stack);
	}
}
