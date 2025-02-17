package io.github.viciscat.kustweaks.mixin.cofh;

import cofh.core.init.CorePotions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CorePotions.class)
public class CorePotionsMixin {

    @Shadow(remap = false) public static PotionType resistance;

    @WrapOperation(method = "createStrongPotionTypes(Lnet/minecraftforge/event/RegistryEvent$Register;Lnet/minecraft/potion/PotionType;IILjava/lang/String;)V", remap = false,
            at = @At(value = "NEW", target = "(Lnet/minecraft/potion/Potion;II)Lnet/minecraft/potion/PotionEffect;", remap = true)
    )
    private PotionEffect nerfResistance4(Potion potionIn, int durationIn, int amplifierIn, Operation<PotionEffect> original, @Local(argsOnly = true) PotionType type) {
        if (type == resistance && amplifierIn == 3) {
            return original.call(potionIn, 3*20, amplifierIn);
        }
        return original.call(potionIn, durationIn, amplifierIn);
    }
}
