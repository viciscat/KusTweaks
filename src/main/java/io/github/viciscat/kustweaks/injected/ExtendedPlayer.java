package io.github.viciscat.kustweaks.injected;

import net.minecraft.entity.player.EntityPlayer;

public interface ExtendedPlayer {


	void kusTweaks$setMagnetTicked(boolean magnetTicked);

	boolean kusTweaks$hasMagnetTicked();


	static ExtendedPlayer of(EntityPlayer player) {
		return (ExtendedPlayer) player;
	}
}
