package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import electroblob.wizardry.item.ItemWand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemWand.class)
public class ItemWandMixin {

    @ModifyReturnValue(method = "isEnchantable", at = @At("RETURN"))
    private boolean isEnchantable(boolean original) {
        return true;
    }

    @ModifyReturnValue(method = "isBookEnchantable", at = @At("RETURN"), remap = false)
    private boolean isBookEnchantable(boolean original) {
        return true;
    }
}
