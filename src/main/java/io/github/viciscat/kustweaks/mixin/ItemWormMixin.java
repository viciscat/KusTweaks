package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.ellpeck.actuallyadditions.mod.items.ItemWorm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemWorm.class, remap = false)
public class ItemWormMixin {

    @ModifyExpressionValue(
            method = "onHoe", remap = false,
            at = @At(value = "CONSTANT", args = "floatValue=0.95")
    )
    private float onHoeModify(float original) {
        return 0.9988f;
    }
}
