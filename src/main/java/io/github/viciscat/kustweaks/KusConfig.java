package io.github.viciscat.kustweaks;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DimensionType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;

public class KusConfig {

	private static final List<DimensionalDebuffRaw> rawDimensionalDebuffs = new ArrayList<>();
	private static EnumMap<DimensionType, List<Debuff>> DEBUFF_MAP = new EnumMap<>(DimensionType.class);

	public static double veryLargeChance = 0.01;
	public static double verySmallChance = 0.01;
	public static double veryLargeChanceOther = 0.01;
	public static double verySmallChanceOther = 0.01;

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
			e.printStackTrace();
			return;
		}

		if (config.has("dimensional_debuffs")) {
			JsonArray dimensionalDebuffs = config.getAsJsonArray("dimensional_debuffs");
			for (JsonElement dimensionalDebuff : dimensionalDebuffs) {
				JsonObject dimensionalDebuffObj = dimensionalDebuff.getAsJsonObject();
				DimensionalDebuffRaw e = DimensionalDebuffRaw.parseDimensionalDebuff(dimensionalDebuffObj);
				rawDimensionalDebuffs.add(e);
				System.out.println(e);
			}
		}

		if (config.has("very_large_chance")) veryLargeChance = config.get("very_large_chance").getAsDouble();
		if (config.has("very_small_chance")) verySmallChance = config.get("very_small_chance").getAsDouble();
		if (config.has("very_large_chance_other")) veryLargeChanceOther = config.get("very_small_chance_other").getAsDouble();
		if (config.has("very_small_chance_other")) verySmallChanceOther = config.get("very_small_chance_other").getAsDouble();

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

	private static class PotionRaw {
		private final String potionName;
		private final int amplifier;
		private final int duration;

		public PotionRaw(String potionName, int amplifier, int duration) {
			this.potionName = potionName;
			this.amplifier = amplifier;
			this.duration = duration;
		}

		public static PotionRaw parsePotion(JsonObject potion) {
			String potionName = potion.get("potion").getAsString();
			int amplifier = potion.get("amplifier").getAsInt();
			int duration = potion.get("duration").getAsInt() * 20;
			return new PotionRaw(potionName, amplifier, duration);
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("potionName", potionName)
					.append("amplifier", amplifier)
					.append("duration", duration)
					.toString();
		}
	}

	private static class DimensionalDebuffRaw {
		private final OptionalInt dimensionId;
		private final String dimensionName;
		private final PotionRaw potion;
		private final int timer;

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
			this.dimensionId = OptionalInt.of(dimensionId);
			this.dimensionName = null;
			this.potion = potion;
			this.timer = timer;
		}
		public DimensionalDebuffRaw(String dimensionName, PotionRaw potion, int timer) {
			this.dimensionId = OptionalInt.empty();
			this.dimensionName = dimensionName;
			this.potion = potion;
			this.timer = timer;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("dimensionId", dimensionId)
					.append("dimensionName", dimensionName)
					.append("potion", potion)
					.append("timer", timer)
					.toString();
		}
	}

	public static class Debuff {
		private final PotionEffect potionEffect;
		private final int timer;

		public Debuff(PotionEffect potionEffect, int timer) {
			this.potionEffect = potionEffect;
			this.timer = timer;
		}

		public PotionEffect getPotionEffect() {
			return potionEffect;
		}

		public int getTimer() {
			return timer;
		}
	}
}
