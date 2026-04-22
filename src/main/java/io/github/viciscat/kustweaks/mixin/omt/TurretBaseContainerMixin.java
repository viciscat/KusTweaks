package io.github.viciscat.kustweaks.mixin.omt;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.viciscat.kustweaks.KusItems;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.client.gui.containers.TurretBaseContainer;
import omtteam.openmodularturrets.items.UpgradeMetaItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TurretBaseContainer.class, remap = false)
public class TurretBaseContainerMixin {

	@Definition(id = "UpgradeMetaItem", type = UpgradeMetaItem.class, remap = false)
	@Expression("? instanceof UpgradeMetaItem")
	@ModifyExpressionValue(method = "transferStackInSlot", remap = true, at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean allowCustomItem(boolean original, @Local(name = "stackInSlot") ItemStack stackInSlot) {
		return original || stackInSlot.getItem() == KusItems.itemBulwarkUpgrade;
	}
}
