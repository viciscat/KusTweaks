package io.github.viciscat.kustweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.viciscat.kustweaks.injected.ExtendedWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(World.class)
public class WorldMixin implements ExtendedWorld {
	@Unique
	private static final String SHARE_ID = "kus_tweaks:ignoredBlocks";
	@Unique
	private static final Predicate<BlockPos> DEFAULT_PREDICATE = b -> false;

	@Unique
	private final ThreadLocal<Predicate<BlockPos>> kusTweaks$ignoredBlocks = ThreadLocal.withInitial(() -> DEFAULT_PREDICATE);

	@Override
	public void kusTweaks$setIgnoredRaycastBlocks(Predicate<BlockPos> ignoredRaycastBlocks) {
		kusTweaks$ignoredBlocks.set(ignoredRaycastBlocks);
	}

	@Inject(method = "rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;", at = @At("HEAD"))
	private void storeValue(CallbackInfoReturnable<RayTraceResult> cir, @Share(SHARE_ID) LocalRef<Predicate<BlockPos>> share) {
		share.set(kusTweaks$ignoredBlocks.get());
	}

	@WrapOperation(
			method = "rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;")
	)
	private IBlockState ignoreBlock(World instance, BlockPos blockPos, Operation<IBlockState> original, @Share(SHARE_ID) LocalRef<Predicate<BlockPos>> share) {
		return share.get().test(blockPos) ? Blocks.AIR.getDefaultState() : original.call(instance, blockPos);
	}

	@Inject(method = "rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;", at = @At("RETURN"))
	private void resetThreadLocal(CallbackInfoReturnable<RayTraceResult> cir) {
		kusTweaks$ignoredBlocks.remove();
	}
}
