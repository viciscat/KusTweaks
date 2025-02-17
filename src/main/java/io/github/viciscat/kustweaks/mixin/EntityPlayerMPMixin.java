package io.github.viciscat.kustweaks.mixin;

import io.github.viciscat.kustweaks.injected.ExtendedPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityPlayerMP.class)
public class EntityPlayerMPMixin implements ExtendedPlayerMP {

    @Unique
    private boolean kus_tweaks$randomRespawn = false;

    @Override
    public void kus_tweaks$setRandomRespawn(boolean randomRespawn) {
        kus_tweaks$randomRespawn = randomRespawn;
    }

    @Override
    public boolean kus_tweaks$getRandomRespawn() {
        return kus_tweaks$randomRespawn;
    }
}
