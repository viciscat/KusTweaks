package io.github.viciscat.kustweaks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

@Mod.EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new HyaloclastiteBlock());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new HyaloclastiteBlock.Item(KusTweaksMod.hyaloclastiteBlock));
    }

    @SubscribeEvent
    public static void changeCobbleToHyaloclastite(BlockEvent.FluidPlaceBlockEvent event) {
        if (event.getState().equals(Blocks.COBBLESTONE.getDefaultState()) || event.getState().equals(Blocks.STONE.getDefaultState()))
            event.setNewState(KusTweaksMod.hyaloclastiteBlock.getDefaultState());
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;
        DimensionType dimensionType = event.world.provider.getDimensionType();
        List<KusConfig.Debuff> debuffs = KusConfig.getDebuffs(dimensionType);
        for (EntityPlayer playerEntity : event.world.playerEntities) {
            if (playerEntity.dimension != dimensionType.getId() || playerEntity.capabilities.isCreativeMode) continue;
            int ticksExisted = playerEntity.ticksExisted;
            for (KusConfig.Debuff debuff : debuffs) {
                if (ticksExisted % debuff.getTimer() == 0) {
                    playerEntity.addPotionEffect(new PotionEffect(debuff.getPotionEffect()));
                }
            }
        }
    }
}
