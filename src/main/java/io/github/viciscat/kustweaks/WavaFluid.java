package io.github.viciscat.kustweaks;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

public class WavaFluid extends Fluid {
	public WavaFluid(String fluidName, ResourceLocation still, ResourceLocation flowing) {
		super(fluidName, still, flowing, new Color(-1));
		setUnlocalizedName(KusTweaksMod.MOD_ID + "." + fluidName);
		this.setDensity(1500); // thicker than a bowl of oatmeal
		this.setViscosity(1500); // sloooow moving
		this.setTemperature(1000); // not exactly lava, but still hot. Should depend on the material
	}

	@Override
	public boolean doesVaporize(FluidStack fluidStack) {
		return false;
	}
}
