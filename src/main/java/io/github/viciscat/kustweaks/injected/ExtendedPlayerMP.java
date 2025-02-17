package io.github.viciscat.kustweaks.injected;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ExtendedPlayerMP {

    void kus_tweaks$setRandomRespawn(boolean randomRespawn);

    boolean kus_tweaks$getRandomRespawn();

    static ExtendedPlayerMP of(EntityPlayerMP playerMP) {
        return (ExtendedPlayerMP) playerMP;
    }
}
