package io.github.viciscat.kustweaks.injected;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Predicate;

public interface ExtendedWorld {

	void kusTweaks$setIgnoredRaycastBlocks(Predicate<BlockPos> ignoredRaycastBlocks);

	static void ignore(World world, Predicate<BlockPos> ignoredRaycastBlocks) {
		((ExtendedWorld) world).kusTweaks$setIgnoredRaycastBlocks(ignoredRaycastBlocks);
	}
}
