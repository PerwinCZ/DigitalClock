package cz.perwin.digitalclock.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.perwin.digitalclock.core.CommandInfo;

public class CommandReload {
	public CommandReload(CommandInfo info, CommandSender sender) {
		if(info.getArgs().length != 1) {
			sender.sendMessage(ChatColor.DARK_RED + info.getMain().getMessagePrefix() + ChatColor.RED + " Correct usage: '/"+ info.getUsedcmd() + " reload'");
		} else if(sender instanceof Player && !info.getPlayer().hasPermission("digitalclock.reload") && !info.getPlayer().isOp()) {
			info.getPlayer().sendMessage(ChatColor.DARK_RED + info.getMain().getMessagePrefix() + ChatColor.RED + " You aren't allowed to use this command!");
		} else {	
			info.getMain().reloadConf();
			sender.sendMessage(ChatColor.DARK_GREEN + info.getMain().getMessagePrefix() + ChatColor.GREEN + " File config.yml has been reloaded!");
		}
	}
}
