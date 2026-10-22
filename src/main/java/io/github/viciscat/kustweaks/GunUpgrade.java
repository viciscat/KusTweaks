package io.github.viciscat.kustweaks;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record GunUpgrade(ResourceLocation id, String name, int maxStacks) {

	@Override
	public String toString() {
		return name;
	}
}
