package io.github.viciscat.kustweaks.network;

import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class KusNetwork {

    private KusNetwork() {}

    public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(KusTweaksMod.MOD_ID);

    public static void init() {
        NETWORK_WRAPPER.registerMessage(RandomRespawnPacket.Handler.class, RandomRespawnPacket.class, 0, Side.SERVER);
    }

}
