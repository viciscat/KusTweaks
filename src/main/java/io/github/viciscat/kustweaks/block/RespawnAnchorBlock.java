package io.github.viciscat.kustweaks.block;

import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class RespawnAnchorBlock extends Block {
    public RespawnAnchorBlock() {
        super(Material.ROCK, MapColor.EMERALD);
        setTranslationKey(KusTweaksMod.MOD_ID + ".respawn_anchor");
        setRegistryName("respawn_anchor");        // The unique name (within your mod) that identifies this block
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;
        playerIn.setSpawnPoint(pos, false);
        playerIn.sendStatusMessage(new TextComponentString("Respawn point set!"), true);
        return true;
    }
}
