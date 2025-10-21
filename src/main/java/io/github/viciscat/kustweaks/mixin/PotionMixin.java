package io.github.viciscat.kustweaks.mixin;

import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Potion.class)
public class PotionMixin {

    @ModifyArg(method = "performEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 0), index = 0)
    private DamageSource changeDamageSource(DamageSource source) {
        return KusTweaksMod.POISON;
    }
}
