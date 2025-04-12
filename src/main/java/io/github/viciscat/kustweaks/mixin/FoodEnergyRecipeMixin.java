package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.rwtema.extrautils2.machine.FoodEnergyRecipe")
public class FoodEnergyRecipeMixin {

    @ModifyExpressionValue(
            method = "getEnergyOutput", remap = false,
            at = @At(value = "CONSTANT", args = "floatValue=8000.0")
    )
    public float nerfEnergyOutput(float input) {
        return 400f;
    }
}
