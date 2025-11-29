package io.github.viciscat.kustweaks.capability;

import io.github.viciscat.kustweaks.GunUpgrade;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ConstantValue")
public class UpgradableGunImpl implements UpgradableGun, ICapabilitySerializable<NBTTagCompound> {
	private static final int FIRST_LEVEL_REQUIREMENT = 500;
	private @Nonnull UUID uuid = MathHelper.getRandomUUID(RANDOM);
	private long xp;
	private final Reference2IntMap<GunUpgrade> upgrades = new Reference2IntOpenHashMap<>(GunUpgrade.values().length, 0.99f);

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setUniqueId("uuid", uuid);
		NBTTagCompound upgradesTag = compound.getCompoundTag("upgrades");
		for (Reference2IntMap.Entry<GunUpgrade> entry : upgrades.reference2IntEntrySet()) {
			upgradesTag.setInteger(entry.getKey().id(), entry.getIntValue());
		}
		compound.setTag("upgrades", upgradesTag);
		compound.setLong("xp", xp);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasUniqueId("uuid")) uuid = Objects.requireNonNull(nbt.getUniqueId("uuid"));
		if (nbt.hasKey("xp")) xp = nbt.getLong("xp");
		upgrades.clear();
		NBTTagCompound upgradesTag = nbt.getCompoundTag("upgrades");
		for (String key : upgradesTag.getKeySet()) {
			GunUpgrade fromId = GunUpgrade.fromId(key);
			if (fromId == null) continue;
			upgrades.put(fromId, upgradesTag.getInteger(key));
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
	public @Nonnull UUID getUUID() {
		return uuid;
	}

	@Override
	public boolean addUpgrade(GunUpgrade upgrade) {
		if (getUsedUpgradeSlots() >= getAvailableUpgradeSlots()) return false;
		int stacks = upgrades.getInt(upgrade);
		if (stacks >= upgrade.maxStacks()) return false;
		upgrades.put(upgrade, stacks + 1);
		return true;
	}

	@Override
	public void removeOneUpgradeStack(GunUpgrade upgrade) {
		int i = upgrades.getInt(upgrade);
		if (i <= 1) upgrades.removeInt(upgrade);
		else upgrades.put(upgrade, i - 1);
	}

	@Override
	public void removeAllUpgradeStacks(GunUpgrade upgrade) {
		upgrades.removeInt(upgrade);
	}

	@Override
	public int getUpgradeStack(GunUpgrade upgrade) {
		return upgrades.getInt(upgrade);
	}

	@Override
	public void addXp(int xp) {
		this.xp += xp;
	}

	@Override
	public List<String> getTooltip(boolean showUpgradesList) {
		ArrayList<String> list = new ArrayList<>();
		list.add("Upgrades: " + getUsedUpgradeSlots() + "/"  + getAvailableUpgradeSlots());
		list.add("Next slot in: " + getXpUntilNextLevel() + " xp");
		if (showUpgradesList) {
			for (Reference2IntMap.Entry<GunUpgrade> entry : upgrades.reference2IntEntrySet()) {
				if (entry.getIntValue() <= 0) continue;
				list.add(entry.getKey() + " x" + entry.getIntValue());
			}
		}
		return list;
	}

	private int getUsedUpgradeSlots() {
		int slots = 0;
		for (Reference2IntMap.Entry<GunUpgrade> entry : upgrades.reference2IntEntrySet()) {
			slots += entry.getIntValue();
		}
		return slots;
	}

	private long getXpUntilNextLevel() {
		long xp = this.xp;
		long requirement = FIRST_LEVEL_REQUIREMENT;
		xp -= requirement;
		while (xp > 0) {
			requirement *= 2;
			xp -= requirement;
		}
		return -xp;
	}

	private int getAvailableUpgradeSlots() {
		int slots = 1;
		long xp = this.xp;
		long requirement = FIRST_LEVEL_REQUIREMENT;
		xp -= requirement;
		while (xp > 0) {
			slots++;
			requirement *= 2;
			xp -= requirement;
		}
		return slots;
	}

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof UpgradableGunImpl that)) return false;

		return uuid.equals(that.uuid) && xp == that.xp && upgrades.equals(that.upgrades);
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
}
