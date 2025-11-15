package io.github.viciscat.kustweaks;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DimensionType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;

public class KusConfig {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final List<DimensionalDebuffRaw> rawDimensionalDebuffs = new ArrayList<>();
	private static EnumMap<DimensionType, List<Debuff>> DEBUFF_MAP = new EnumMap<>(DimensionType.class);

	public static double veryLargeChance = 0.01;
	public static double verySmallChance = 0.01;
	public static double veryLargeChanceOther = 0.01;
	public static double verySmallChanceOther = 0.01;

	public static final Set<String> kindlingDamageSources = new TreeSet<>();
	public static final Object2IntMap<String> chunkLoadEntityCap = new Object2IntOpenHashMap<>();
	public static final Set<DimensionType> respawnDisabledDimensions = EnumSet.noneOf(DimensionType.class);

	public static List<Debuff> getDebuffs(DimensionType type) {
		return DEBUFF_MAP.getOrDefault(type, ImmutableList.of());
	}

	public static void loadConfig(File configFile) {
		JsonObject config;
		try (BufferedReader reader = Files.newBufferedReader(configFile.toPath())) {
			config = KusTweaksMod.GSON.fromJson(reader, JsonObject.class);
		} catch (NoSuchFileException e) {
			return;
		} catch (IOException e) {
			LOGGER.error("Failed to load config file", e);
			return;
		}
		kindlingDamageSources.clear();
		kindlingDamageSources.add("tg_fire");

		if (config.has("kindling_damage_sources")) {
			for (JsonElement source : config.getAsJsonArray("kindling_damage_sources")) {
				kindlingDamageSources.add(source.getAsString());
			}
		}

		if (config.has("chunk_load_entity_cap")) {
			for (Map.Entry<String, JsonElement> entityCap : config.getAsJsonObject("chunk_load_entity_cap").entrySet()) {
				chunkLoadEntityCap.put(entityCap.getKey(), entityCap.getValue().getAsInt());
			}
		}

		if (config.has("dimensional_debuffs")) {
			JsonArray dimensionalDebuffs = config.getAsJsonArray("dimensional_debuffs");
			for (JsonElement dimensionalDebuff : dimensionalDebuffs) {
				JsonObject dimensionalDebuffObj = dimensionalDebuff.getAsJsonObject();
				DimensionalDebuffRaw e = DimensionalDebuffRaw.parseDimensionalDebuff(dimensionalDebuffObj);
				rawDimensionalDebuffs.add(e);
			}
		}

		if (config.has("very_large_chance")) veryLargeChance = config.get("very_large_chance").getAsDouble();
		if (config.has("very_small_chance")) verySmallChance = config.get("very_small_chance").getAsDouble();
		if (config.has("very_large_chance_other")) veryLargeChanceOther = config.get("very_large_chance_other").getAsDouble();
		if (config.has("very_small_chance_other")) verySmallChanceOther = config.get("very_small_chance_other").getAsDouble();
		if (config.has("respawn_disabled_dimensions")) {
			JsonArray arr = config.getAsJsonArray("respawn_disabled_dimensions");
			for (JsonElement e : arr) {
				if (!e.isJsonPrimitive()) continue;
				JsonPrimitive primitive = e.getAsJsonPrimitive();
				if (primitive.isString()) respawnDisabledDimensions.add(DimensionType.byName(e.getAsString()));
				if (primitive.isNumber()) respawnDisabledDimensions.add(DimensionType.getById(e.getAsInt()));
			}
		}

	}

	public static void finalizeConfig() {
		DEBUFF_MAP = new EnumMap<>(DimensionType.class);
		for (DimensionalDebuffRaw rawDimensionalDebuff : rawDimensionalDebuffs) {
			DimensionType dimensionType;
			if (rawDimensionalDebuff.dimensionId.isPresent()) {
				dimensionType = DimensionType.getById(rawDimensionalDebuff.dimensionId.getAsInt());
			} else {
				dimensionType = DimensionType.byName(rawDimensionalDebuff.dimensionName);
			}
			Potion potion = Potion.getPotionFromResourceLocation(rawDimensionalDebuff.potion.potionName);
			if (potion == null) continue;
			PotionEffect potionEffect = new PotionEffect(potion, rawDimensionalDebuff.potion.duration, rawDimensionalDebuff.potion.amplifier, true, false);
			DEBUFF_MAP.computeIfAbsent(dimensionType, dimensionType1 -> new ArrayList<>()).add(new Debuff(potionEffect, rawDimensionalDebuff.timer));
		}
	}

	private record PotionRaw(String potionName, int amplifier, int duration) {
		public static PotionRaw parsePotion(JsonObject potion) {
			String potionName = potion.get("potion").getAsString();
			int amplifier = potion.get("amplifier").getAsInt();
			int duration = potion.get("duration").getAsInt() * 20;
			return new PotionRaw(potionName, amplifier, duration);
		}
	}

	private record DimensionalDebuffRaw(OptionalInt dimensionId, String dimensionName, PotionRaw potion, int timer) {
		public static DimensionalDebuffRaw parseDimensionalDebuff(JsonObject debuff) {
			JsonPrimitive dimension = debuff.get("dimension").getAsJsonPrimitive();
			PotionRaw potionRaw = PotionRaw.parsePotion(debuff.getAsJsonObject("effect"));
			int timer = debuff.get("timer").getAsInt();
			if (dimension.isNumber()) {
				return new DimensionalDebuffRaw(dimension.getAsInt(), potionRaw, timer);
			} else {
				return new DimensionalDebuffRaw(dimension.getAsString(), potionRaw, timer);
			}
		}

		public DimensionalDebuffRaw(int dimensionId, PotionRaw potion, int timer) {
			this(OptionalInt.of(dimensionId), null, potion, timer);
		}

		public DimensionalDebuffRaw(String dimensionName, PotionRaw potion, int timer) {
			this(OptionalInt.empty(), dimensionName, potion, timer);
		}
	}

	public record Debuff(PotionEffect potionEffect, int timer) {}
}
