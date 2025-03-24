package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.lellson.roughmobs2.ai.combat.RoughAISummonSkeleton;
import net.minecraft.entity.monster.AbstractSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoughAISummonSkeleton.class)
public class SkeletonSummonMixin {

    @Inject(method = "startExecuting", at = @At("TAIL"))
    private void startExecuting(CallbackInfo ci, @Local AbstractSkeleton skeleton) {
        skeleton.getEntityData().setBoolean("CannotDropLoot", true);
    }
}
