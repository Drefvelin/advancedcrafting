package net.tfminecraft.AdvancedCrafting.Managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CommandManager implements Listener, CommandExecutor{
	public String cmd1 = "advancedcrafting";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase(cmd1) && args[0].equalsIgnoreCase("test") && args.length == 1) {
				p.sendMessage("bing");
			}
		}
		return false;
	}
}
