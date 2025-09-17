package io.github.viciscat.kustweaks.mixin.cofh;

import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ExtruderManager.class, remap = false)
public class ExtruderManagerMixin {

    @ModifyArg(
            method = "initialize",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/block/Block;)V", ordinal = 0, remap = true),
            index = 0)
    private static Block replaceCobble(Block par1) {
        return KusTweaksMod.hyaloclastiteBlock;
    }

    @ModifyArg(method = "initialize",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidStack;<init>(Lnet/minecraftforge/fluids/Fluid;I)V", ordinal = 2),
            index = 1)
    private static int requireLavaForStone(int amount) {
        return 900;
    }

    @ModifyArg(method = "initialize",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidStack;<init>(Lnet/minecraftforge/fluids/Fluid;I)V", ordinal = 6),
            index = 1)
    private static int requireLavaForStone1(int amount) {
        return 900;
    }

    @ModifyArg(method = "initialize",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidStack;<init>(Lnet/minecraftforge/fluids/Fluid;I)V", ordinal = 8),
            index = 1)
    private static int requireLavaForStone3(int amount) {
        return 900;
    }

    @ModifyArg(method = "initialize",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidStack;<init>(Lnet/minecraftforge/fluids/Fluid;I)V", ordinal = 10),
            index = 1)
    private static int requireLavaForStone5(int amount) {
        return 900;
    }
}
