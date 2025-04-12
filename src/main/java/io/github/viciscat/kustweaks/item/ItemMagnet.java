package io.github.viciscat.kustweaks.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

import java.util.List;

public class ItemMagnet extends Item implements IBauble {

	public ItemMagnet() {
		super();
		setRegistryName(KusTweaksMod.MOD_ID, "item_magnet");
		setTranslationKey(KusTweaksMod.MOD_ID + ".item_magnet");
		setCreativeTab(CreativeTabs.TOOLS);
		setMaxStackSize(1);
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.BELT;
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		IBauble.super.onWornTick(itemstack, player);
		if (player.world.isRemote) return;
		if (player instanceof EntityPlayer) {
			EntityPlayer playerEntity = (EntityPlayer) player;
			pickupItems(playerEntity);
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
		if (worldIn.isRemote) return;
		if (entityIn instanceof EntityPlayer) {
			EntityPlayer playerEntity = (EntityPlayer) entityIn;
			pickupItems(playerEntity);
		}
	}

	@SuppressWarnings({"ConstantValue", "DataFlowIssue"})
	private void pickupItems(EntityPlayer player) {
		if (player.isDead) return;
		List<EntityItem> entitiesWithinAABB = player.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, player.getEntityBoundingBox().grow(2, 1, 2));
		for (EntityItem item : entitiesWithinAABB) {
			if (!item.isDead) {
				if (item.cannotPickup()) continue;
				ItemStack itemstack = item.getItem();
				int i = itemstack.getCount();

				int hook = net.minecraftforge.event.ForgeEventFactory.onItemPickup(item, player);
				if (hook < 0)
					continue;
				ItemStack clone = itemstack.copy();

				if (!item.cannotPickup() && (item.getOwner() == null || item.lifespan - item.getAge() <= 200 || item.getOwner()
						.equals(player.getName())) && (hook == 1 || i <= 0 || player.inventory.addItemStackToInventory(itemstack) || clone.getCount() > item.getItem().getCount())) {
					clone.setCount(clone.getCount() - item.getItem().getCount());
					net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerItemPickupEvent(player, item, clone);

					if (itemstack.isEmpty()) {
						player.onItemPickup(item, i);
						item.setDead();
						itemstack.setCount(i);
					}

					player.addStat(StatList.getObjectsPickedUpStats(itemstack.getItem()), i);
				}
			}
		}
	}
}
