package io.github.viciscat.kustweaks;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum GunUpgrade {
	AMMO,
	BOUNCE,
	DAMAGE,
	EXPLOSION_SIZE,
	FIRE_RATE,
	MULTISHOT,
	PIERCE,
	REFUND_CHANCE,
	RELOAD_SPEED;

	private static final String[] IDS = Arrays.stream(values()).map(Enum::name).map(s -> s.toLowerCase(Locale.ENGLISH)).toArray(String[]::new);
	private static final Map<String, GunUpgrade> FROM_ID = Arrays.stream(values()).collect(Collectors.toMap(GunUpgrade::id, Function.identity()));

	private final int maxStacks;

	GunUpgrade(int maxStacks) {
		this.maxStacks = maxStacks;
	}

	GunUpgrade() {
		this(Integer.MAX_VALUE);
	}

	public String id() {
		return IDS[ordinal()];
	}

	public ResourceLocation model() {
		return new ResourceLocation(KusTweaksMod.MOD_ID, "gun_upgrade/" + this.id());
	}

	public static @Nullable GunUpgrade fromId(final String id) {
		return FROM_ID.get(id);
	}

	public int maxStacks() {
		return maxStacks;
	}
}
