package io.github.viciscat.kustweaks.mixin;

import arekkuusu.enderskills.common.entity.placeable.EntityFinalFlash;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = EntityFinalFlash.class, remap = false)
public class EntityFinalFlashMixin {

    @ModifyArg(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Larekkuusu/enderskills/common/skill/effect/Slowed;set(Lnet/minecraft/entity/EntityLivingBase;Larekkuusu/enderskills/api/capability/data/SkillData;D)V", remap = false),
            index = 2,
            remap = true
    )
    private double slowBigTime(double slow) {
        return 0.95;
    }
}
