package io.github.viciscat.kustweaks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HyaloclastiteBlock extends Block {

    public HyaloclastiteBlock() {
        super(Material.ROCK);
        setTranslationKey(KusTweaksMod.MOD_ID + ".hyaloclastite");
        setRegistryName("hyaloclastite");        // The unique name (within your mod) that identifies this block
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    public static class Item extends ItemBlock {

        public Item(Block block) {
            super(block);
            setRegistryName(block.getRegistryName());
        }

        @Override
        public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
            super.addInformation(stack, worldIn, tooltip, flagIn);
            tooltip.add("Cobblestone!");
            tooltip.add("...");
            tooltip.add("Or is it?");
        }
    }


}
