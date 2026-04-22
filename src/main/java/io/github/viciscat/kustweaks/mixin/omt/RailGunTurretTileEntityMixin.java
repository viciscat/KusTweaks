package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.tileentity.turrets.RailGunTurretTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import techguns.TGItems;

@Mixin(value = RailGunTurretTileEntity.class, remap = false)
public class RailGunTurretTileEntityMixin {

	@ModifyReturnValue(method = "getAmmo", at = @At("RETURN"))
	private ItemStack getAmmo(ItemStack stack) {
		return TGItems.GAUSSRIFLE_SLUGS;
	}
}
