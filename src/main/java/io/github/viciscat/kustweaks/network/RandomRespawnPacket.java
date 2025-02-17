package io.github.viciscat.kustweaks.network;

import io.github.viciscat.kustweaks.injected.ExtendedPlayerMP;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RandomRespawnPacket implements IMessage {

    public RandomRespawnPacket() {}

    @Override
    public void fromBytes(ByteBuf byteBuf) {}

    @Override
    public void toBytes(ByteBuf byteBuf) {}

    public static class Handler implements IMessageHandler<RandomRespawnPacket, IMessage> {

        @Override
        public IMessage onMessage(RandomRespawnPacket message, MessageContext context) {
            FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> handle(message, context));

            return null;
        }

        private void handle(RandomRespawnPacket message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.markPlayerActive();
            if (player.getHealth() > 0.0f) {
                return;
            }
            ExtendedPlayerMP.of(player).kus_tweaks$setRandomRespawn(true);
            context.getServerHandler().player = player = player.server.getPlayerList().recreatePlayerEntity(player, player.dimension, false);
            if (!player.server.isHardcore()) return;
            player.setGameType(GameType.SPECTATOR);
            player.getServerWorld().getGameRules().setOrCreateGameRule("spectatorsGenerateChunks", "false");
        }
    }
}
