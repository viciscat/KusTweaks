package io.github.viciscat.kustweaks;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GunUpgrades {
	private static final Map<ResourceLocation, GunUpgrade> upgrades = new HashMap<>();
	static final List<GunUpgrade> KUS_GUN_UPGRADES = new ArrayList<>();

	public static final GunUpgrade AMMO = registerKus("ammo", "Ammo");
	public static final GunUpgrade BOUNCE = registerKus("bounce", "§eBounces§r");
	public static final GunUpgrade DAMAGE = registerKus("damage", "§cDamage§r");
	public static final GunUpgrade EXPLOSION_SIZE = registerKus("explosion_size", "§cExplosion Size§r");
	public static final GunUpgrade FIRE_RATE = registerKus("fire_rate", "§eFire-rate§r");
	public static final GunUpgrade MULTISHOT = registerKus("multishot", "§eMultishot§r");
	public static final GunUpgrade PIERCE = registerKus("pierce", "Pierce");
	public static final GunUpgrade REFUND_CHANCE = registerKus("refund_chance", "§eRefund Chance§r");
	public static final GunUpgrade RELOAD_SPEED = registerKus("reload_speed", "§eReload Speed§r");
	public static final GunUpgrade PROJECTILE_SPEED = registerKus("projectile_speed", "Projectile Speed");

	private static GunUpgrade registerKus(String id, String name) {
		GunUpgrade register = register(new ResourceLocation(KusTweaksMod.MOD_ID, id), name);
		KUS_GUN_UPGRADES.add(register);
		return register;
	}

	public static GunUpgrade register(ResourceLocation id, String name) {
		return register(id, name, Integer.MAX_VALUE);
	}

	public static GunUpgrade register(ResourceLocation id, String name, int maxStacks) {
		GunUpgrade upgrade = new GunUpgrade(id, name, maxStacks);
		upgrades.put(id, upgrade);
		return upgrade;
	}

	public static @Nullable GunUpgrade get(ResourceLocation id) {
		return upgrades.get(id);
	}
}
