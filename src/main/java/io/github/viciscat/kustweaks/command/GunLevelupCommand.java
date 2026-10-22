package io.github.viciscat.kustweaks.command;

import io.github.viciscat.kustweaks.capability.UpgradableGun;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class GunLevelupCommand extends CommandBase {
	@Override
	public String getName() {
		return "levelupgun";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/levelupgun";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Entity entity = sender.getCommandSenderEntity();
		if (!(entity instanceof EntityLivingBase base)) throw new CommandException("This command should be executed by a living entity!");
		ItemStack mainhand = base.getHeldItemMainhand();
		UpgradableGun gun = UpgradableGun.get(mainhand);
		if (gun == null) throw new CommandException("Don't seem to be holding a gun!");
		long xpUntilNextLevel = gun.getXpUntilNextLevel();
		while (xpUntilNextLevel > Integer.MAX_VALUE) {
			gun.addXp(Integer.MAX_VALUE);
			xpUntilNextLevel -= Integer.MAX_VALUE;
		}
		gun.addXp((int) xpUntilNextLevel);
		sender.sendMessage(new TextComponentString("Gun leveled up successfully!"));
	}
}
