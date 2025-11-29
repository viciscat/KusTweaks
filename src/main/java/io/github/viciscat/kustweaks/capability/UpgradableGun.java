package io.github.viciscat.kustweaks.capability;

import io.github.viciscat.kustweaks.GunUpgrade;
import io.github.viciscat.kustweaks.KusTweaksMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import techguns.items.guns.GenericGun;
import techguns.items.guns.GenericGunMeleeCharge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public interface UpgradableGun extends INBTSerializable<NBTTagCompound> {
	Random RANDOM = new Random();
	@CapabilityInject(UpgradableGun.class)
	Capability<UpgradableGun> CAPABILITY = null;
	ResourceLocation ID = new ResourceLocation(KusTweaksMod.MOD_ID, "upgradable_gun");
	UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	UpgradableGun EMPTY = new UpgradableGun() {
		@Nonnull
		@Override
		public UUID getUUID() {
			return EMPTY_UUID;
		}

		@Override
		public boolean addUpgrade(GunUpgrade upgrade) {
			return false;
		}

		@Override
		public void removeOneUpgradeStack(GunUpgrade upgrade) {

		}

		@Override
		public void removeAllUpgradeStacks(GunUpgrade upgrade) {

		}

		@Override
		public int getUpgradeStack(GunUpgrade upgrade) {
			return 0;
		}

		@Override
		public void addXp(int xp) {

		}

		@Override
		public List<String> getTooltip(boolean showUpgradesList) {
			return List.of();
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return null;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {

		}
	};

	@SuppressWarnings("DataFlowIssue")
	static @Nullable UpgradableGun get(ItemStack stack) {
		return stack.getCapability(CAPABILITY, null);
	}

	static UpgradableGun getOrEmpty(ItemStack stack) {
		UpgradableGun gun = get(stack);
		return gun != null ? gun : EMPTY;
	}

	@Nonnull UUID getUUID();

	/**
	 *
	 * @return true if the upgrade was added.
	 */
	boolean addUpgrade(GunUpgrade upgrade);

	void removeOneUpgradeStack(GunUpgrade upgrade);

	void removeAllUpgradeStacks(GunUpgrade upgrade);

	int getUpgradeStack(GunUpgrade upgrade);

	void addXp(int xp);

	List<String> getTooltip(boolean showUpgradesList);

	class Storage implements Capability.IStorage<UpgradableGun> {

		public static final Storage INSTANCE = new Storage();
		private Storage() {}

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<UpgradableGun> capability, UpgradableGun instance, EnumFacing side) {
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<UpgradableGun> capability, UpgradableGun instance, EnumFacing side, NBTBase nbt) {
			instance.deserializeNBT((NBTTagCompound) nbt);
		}

	}

	@SubscribeEvent
	static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject().isEmpty() || !(event.getObject().getItem() instanceof GenericGun gun) || gun instanceof GenericGunMeleeCharge) return;
		event.addCapability(ID, new UpgradableGunImpl());
	}

	@SubscribeEvent
	static void addTooltip(ItemTooltipEvent event) {
		event.getToolTip().addAll(getOrEmpty(event.getItemStack()).getTooltip(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)));
	}
}
