package io.github.viciscat.kustweaks.mixin;

import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {
    @Shadow public abstract DamageSource setMagicDamage();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(String damageTypeIn, CallbackInfo ci) {
        if (damageTypeIn.equals("skill") || damageTypeIn.equals("indirectSkill")) {
            setMagicDamage();
        }
    }
}
