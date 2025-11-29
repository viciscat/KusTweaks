package io.github.viciscat.kustweaks.capability;

import io.github.viciscat.kustweaks.KusTweaksMod;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("ConstantValue")
public class GunExperienceTrackerImpl implements GunExperienceTracker, ICapabilitySerializable<NBTTagCompound> {
	private @Nullable Map<UUID, UUID> gunToOwnerMap;
	private @Nullable Object2FloatMap<UUID> damageMap;


	private Object2FloatMap<UUID> getOrCreateDamageMap() {
		if (this.damageMap == null) this.damageMap = new Object2FloatOpenHashMap<>(4);
		return this.damageMap;
	}

	private Map<UUID, UUID> getOrCreateGunToOwnerMap() {
		if (this.gunToOwnerMap == null) this.gunToOwnerMap = new Object2ObjectOpenHashMap<>();
		return this.gunToOwnerMap;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound damage = compound.getCompoundTag("damage");
		if (damageMap != null)
			for (Object2FloatMap.Entry<UUID> entry : damageMap.object2FloatEntrySet())
				damage.setFloat(entry.getKey().toString(), entry.getFloatValue());

		compound.setTag("damage", damage);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		NBTTagCompound damage = nbt.getCompoundTag("damage");
		Object2FloatMap<UUID> map = getOrCreateDamageMap();
		for (String s : damage.getKeySet()) {
			try {
				map.put(UUID.fromString(s), damage.getFloat(s));
			} catch (IllegalArgumentException ignored) {} // ignore if the uuid is wrong.
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == CAPABILITY ? CAPABILITY.cast(this) : null;
	}

	@Override
	public void trackDamage(EntityPlayer shooter, UpgradableGun gun, float amount) {
		getOrCreateDamageMap().compute(gun.getUUID(), (stack, aFloat) -> amount + (aFloat == null ? 0 : aFloat));
		getOrCreateGunToOwnerMap().put(gun.getUUID(), shooter.getUniqueID());
	}

	@Override
	public void distributeXp(float maxHealth) {
		if (damageMap == null || damageMap.isEmpty()) return;
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server == null) {
			KusTweaksMod.LOGGER.warn("Server is null");
			return;
		}
		PlayerList playerList = server.getPlayerList();
		for (Object2FloatMap.Entry<UUID> entry : damageMap.object2FloatEntrySet()) {
			UUID gunUuid = entry.getKey();
			float amount = Math.min(entry.getFloatValue(), maxHealth) + maxHealth / 2f;
			UUID playerUuid = getOrCreateGunToOwnerMap().get(gunUuid);
			if (playerUuid != null) {
				EntityPlayerMP player = playerList.getPlayerByUUID(playerUuid);
				if (player != null && tryGiveXp(player, gunUuid, amount)) continue;
			}
			for (EntityPlayerMP player : playerList.getPlayers()) {
				if (tryGiveXp(player, gunUuid, amount)) break;
			}
		}
	}

	private boolean tryGiveXp(EntityPlayerMP player, UUID gunUuid, float amount) {
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			UpgradableGun gun = UpgradableGun.get(stack);
			if (gun == null || !gunUuid.equals(gun.getUUID())) continue;
			gun.addXp((int) amount);
			return true;
		}
		return false;
	}
}
