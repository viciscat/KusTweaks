package io.github.viciscat.kustweaks.mixin.cofh;

import cofh.thermalexpansion.block.device.TileDeviceBase;
import cofh.thermalexpansion.block.device.TileTapper;
import cofh.thermalexpansion.util.managers.device.TapperManager;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.trees.TreeFamilyVanilla;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = TileTapper.class, remap = false)
public abstract class TileTapperMixin extends TileDeviceBase {

    @Shadow
    private BlockPos trunkPos;

    @Shadow
    private boolean cached;

    @Shadow
    private boolean validTree;

    @Shadow
    private FluidStack genFluid;

    @Nullable
    @Unique
    private ItemStack kusTweaks$dynamicWoodType = null;

    @Inject(method = "updateValidity", at = @At(value = "INVOKE", target = "Lcofh/thermalexpansion/util/managers/device/TapperManager;getLeaf(Lnet/minecraft/block/state/IBlockState;)Ljava/util/Set;", ordinal = 0), cancellable = true)
    private void allowDynamic(CallbackInfo ci) {
        if (kusTweaks$testValidDynamicTree()) ci.cancel();
    }

    @Inject(method = "updateValidity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getAllInBoxMutable(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Iterable;", remap = true, ordinal = 2), cancellable = true)
    private void allowDynamic2(CallbackInfo ci) {
        if (kusTweaks$testValidDynamicTree()) ci.cancel();
    }

    @Inject(method = "isTrunkBase", at = @At(value = "HEAD"), cancellable = true)
    private void isTrunkBase(BlockPos checkPos, CallbackInfoReturnable<Boolean> cir) {
        BlockPos down = checkPos.down();
        IBlockState state = this.world.getBlockState(down);
        BlockRooty rooty = TreeHelper.getRooty(state);
        if (rooty == null) return;
        TreeFamily family = rooty.getFamily(state, world, down);
        if (family instanceof TreeFamilyVanilla) {
            TreeFamilyVanilla vanilla = (TreeFamilyVanilla) family;
            int metadata = vanilla.woodType.getMetadata();
            if (metadata < 4) {
                cir.setReturnValue(TapperManager.mappingExists(new ItemStack(Blocks.LOG, 1, metadata)));
            } else {
                cir.setReturnValue(TapperManager.mappingExists(new ItemStack(Blocks.LOG2, 1, metadata - 4)));
            }
        }
    }

    @WrapOperation(
            method = "update",
            at = @At(value = "INVOKE", target = "Lcofh/thermalexpansion/util/managers/device/TapperManager;getFluid(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraftforge/fluids/FluidStack;", remap = false),
            remap = true
    )
    private FluidStack getFluid(IBlockState state, Operation<FluidStack> original) {
        if (kusTweaks$dynamicWoodType == null) return original.call(state);
        return TapperManager.getFluid(kusTweaks$dynamicWoodType);
    }

    /**
     * @return whether the method should return
     */
    @Unique
    private boolean kusTweaks$testValidDynamicTree() {
        kusTweaks$dynamicWoodType = null;
        BlockPos down = trunkPos.down();
        IBlockState state = this.world.getBlockState(down);
        BlockRooty rooty = TreeHelper.getRooty(state);
        if (rooty == null) return false;
        IBlockState trunkState = this.world.getBlockState(trunkPos);
        ITreePart part = TreeHelper.getTreePart(trunkState);
        cached = true;
        validTree = part.getRadius(trunkState) >= 5;
        if (!validTree) return true;
        TreeFamily family = rooty.getFamily(state, world, down);
        if (family instanceof TreeFamilyVanilla) {
            TreeFamilyVanilla vanilla = (TreeFamilyVanilla) family;
            int metadata = vanilla.woodType.getMetadata();
            if (metadata < 4) {
                kusTweaks$dynamicWoodType = new ItemStack(Blocks.LOG, 1, metadata);
            } else {
                kusTweaks$dynamicWoodType = new ItemStack(Blocks.LOG2, 1, metadata - 4);
            }
            genFluid = TapperManager.getFluid(kusTweaks$dynamicWoodType);
        } else {
            KusTweaksMod.LOGGER.warn("Invalid tree family while ticking! That should not happen!");
        }
        return true;
    }
}
