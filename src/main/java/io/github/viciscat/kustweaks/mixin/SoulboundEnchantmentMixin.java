package io.github.viciscat.kustweaks.mixin;

import cofh.core.enchantment.EnchantmentSoulbound;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.api.tginventory.TGSlotType;

@Pseudo
@Mixin(value = EnchantmentSoulbound.class)
public class SoulboundEnchantmentMixin {

    @Shadow(remap = false) public static boolean enable;

    @Unique
    private static final TGSlotType[] kusTweaks$ALLOWED = new TGSlotType[]{TGSlotType.FACESLOT, TGSlotType.BACKSLOT, TGSlotType.AMMOSLOT, TGSlotType.HANDSLOT};

    @ModifyReturnValue(method = "canApply", at = @At(value = "RETURN"))
    private boolean techGunsCompat(boolean original, @Local(argsOnly = true) ItemStack stack) {
        boolean techGuns = false;
        if (stack.getItem() instanceof ITGSpecialSlot) {
            TGSlotType slot = ((ITGSpecialSlot) stack.getItem()).getSlot(stack);
            for (TGSlotType tgSlotType : kusTweaks$ALLOWED) {
                if (tgSlotType.equals(slot)) {
                    techGuns = true;
                    break;
                }
            }
        }
        return original || (techGuns && enable);
    }
}
