package io.github.viciscat.kustweaks.mixin;

import io.github.viciscat.kustweaks.block.RespawnAnchorBlock;
import io.github.viciscat.kustweaks.injected.ExtendedPlayer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin implements ExtendedPlayer {

	@Unique
	private boolean kusTweaks$magnetTicked = false;

	@Override
	public void kusTweaks$setMagnetTicked(boolean magnetTicked) {
		this.kusTweaks$magnetTicked = magnetTicked;
	}

	@Override
	public boolean kusTweaks$hasMagnetTicked() {
		return kusTweaks$magnetTicked;
	}

	@Inject(method = "getBedSpawnLocation", at = @At("HEAD"), cancellable = true)
	private static void kusTweaks$getRespawnAnchorLocation(World worldIn, BlockPos bedLocation, boolean forceSpawn, CallbackInfoReturnable<BlockPos> cir) {
		IBlockState state = worldIn.getBlockState(bedLocation);
		Block block = state.getBlock();
		if (block instanceof RespawnAnchorBlock) {
			cir.setReturnValue(bedLocation.add(0, 1, 0));
		}
	}
}
