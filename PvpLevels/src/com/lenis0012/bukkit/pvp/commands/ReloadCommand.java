package com.lenis0012.bukkit.pvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.lenis0012.bukkit.pvp.PvpLevels;

public class ReloadCommand implements CommandExecutor {
	private PvpLevels plugin;
	
	public ReloadCommand(PvpLevels plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("pvplevels.reload")) {
			sender.sendMessage("\2474You don't have permission for this command!");
			return true;
		}
		plugin.reload();
		sender.sendMessage("\247aConfig and rewards reloaded!");
		return true;
	}
}