package io.github.viciscat.kustweaks.mixin;

import baubles.api.IBauble;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.enchantment.EnumEnchantmentType$12")
public class EnumEnchantmentTypeMixin {


    @ModifyReturnValue(method = "canEnchantItem", at = @At("RETURN"))
    private boolean baublesSupport(boolean original, @Local(argsOnly = true) Item itemIn) {
        return original || itemIn instanceof IBauble;
    }
}
