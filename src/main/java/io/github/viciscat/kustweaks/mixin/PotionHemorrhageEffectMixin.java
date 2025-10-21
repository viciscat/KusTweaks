package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.viciscat.kustweaks.injected.ExtendedHemorrhageEffect;
import net.minecraftforge.common.config.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import uvmidnight.totaltinkers.newweapons.potion.PotionHemorrhageEffect;

@Mixin(value = PotionHemorrhageEffect.class, remap = false)
public class PotionHemorrhageEffectMixin implements ExtendedHemorrhageEffect {

    @Unique
    private float kusTweaks$toolDamage;

    @Override
    public void kus_tweaks$setToolDamage(float toolDamage) {
        this.kusTweaks$toolDamage = toolDamage;
    }

    @WrapOperation(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/config/Property;getDouble()D", ordinal = 0), remap = true)
    private double onUpdate$getDouble(Property instance, Operation<Double> original) {
        return original.call(instance) * Math.max(1.0, Math.pow(kusTweaks$toolDamage / 5.0, 0.676));
    }
}
