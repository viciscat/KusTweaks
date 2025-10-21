package io.github.viciscat.kustweaks.mixin;

import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = SRPEffectBase.class, remap = false)
public class SRPEffectBaseMixin {

    @ModifyArg(method = "performEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 0), index = 0, remap = true)
    private DamageSource changeDamageSource(DamageSource source) {
        return KusTweaksMod.PARASITE_BLEED;
    }
}
