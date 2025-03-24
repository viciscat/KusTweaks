package io.github.viciscat.kustweaks.mixin;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.github.viciscat.kustweaks.KusAttributes;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityParasiteBase.class)
public class EntityParasiteBaseMixin {
    @Shadow(remap = false) protected float MiniDamage;

    @Inject(method = "attackEntityAsMobMinimum", at = @At("HEAD"), remap = false)
    private void editMiniDamage(EntityLivingBase target, CallbackInfoReturnable<Boolean> cir, @Share("minDamage") LocalFloatRef ref) {
        ref.set(MiniDamage);
        MiniDamage *= (float)(1 - KusAttributes.getAttributeOrDefault(target, KusAttributes.TRUE_HEAL_PERCENT_DAMAGE));

    }
    @Inject(method = "attackEntityAsMobMinimum", at = @At("RETURN"), remap = false)
    private void resetMiniDamage(EntityLivingBase target, CallbackInfoReturnable<Boolean> cir, @Share("minDamage") LocalFloatRef ref) {
        MiniDamage = ref.get();
    }
}
