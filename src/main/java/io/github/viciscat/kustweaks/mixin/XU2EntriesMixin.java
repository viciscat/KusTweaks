package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.rwtema.extrautils2.backend.entries.XU2Entries$9", remap = false)
public class XU2EntriesMixin {
    @WrapOperation(method = "postInit", at = @At(value = "INVOKE", target = "Lcom/rwtema/extrautils2/eventhandlers/RareSeedHandler;register(Lnet/minecraft/item/ItemStack;D)V"), remap = false)
    private void removeLilly(ItemStack seedEntry, double seed, Operation<Void> original) {

    }
}
