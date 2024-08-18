package io.github.viciscat.kustweaks;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Item itemFromBlock = Item.getItemFromBlock(KusTweaksMod.hyaloclastiteBlock);
        ModelLoader.setCustomModelResourceLocation(itemFromBlock, 0, new ModelResourceLocation(itemFromBlock.getRegistryName(), "inventory"));
    }
}
