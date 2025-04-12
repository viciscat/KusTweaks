package io.github.viciscat.kustweaks.mixin;

import io.github.viciscat.kustweaks.block.RespawnAnchorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends Entity {

	public EntityPlayerMixin(World worldIn) {
		super(worldIn);
	}

	@Shadow(remap = false) public abstract void setSpawnDimension(@Nullable Integer dimension);

	@Inject(method = "getBedSpawnLocation", at = @At("HEAD"), cancellable = true)
	private static void kusTweaks$getRespawnAnchorLocation(World worldIn, BlockPos bedLocation, boolean forceSpawn, CallbackInfoReturnable<BlockPos> cir) {
		IBlockState state = worldIn.getBlockState(bedLocation);
		Block block = state.getBlock();
		if (block instanceof RespawnAnchorBlock) {
			cir.setReturnValue(bedLocation.add(0, 1, 0));
		}
	}

	@Inject(method = "setSpawnPoint", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;dimension:I", opcode = Opcodes.GETFIELD, ordinal = 0))
	private void setRespawnAnchorLocation(BlockPos pos, boolean forced, CallbackInfo ci) {
		setSpawnDimension(dimension);
	}
}
