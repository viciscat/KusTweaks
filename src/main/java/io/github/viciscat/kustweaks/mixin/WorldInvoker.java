package io.github.viciscat.kustweaks.mixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface WorldInvoker {
    @Invoker("isChunkLoaded")
    boolean invokeIsChunkLoaded(int x, int z, boolean allowEmpty);
}
