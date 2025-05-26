package net.tfminecraft.AdvancedCrafting.Managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.tfminecraft.AdvancedCrafting.AdvancedCrafting;

public class CommandManager implements Listener, CommandExecutor{
	public String cmd1 = "ac";
	public String cmd2 = "alloy";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase(cmd2) && args[0].equalsIgnoreCase("name") && args.length >= 1) {
				if(args.length < 2) {
					p.sendMessage("§cNo name specified, format: /alloy name newname");
					return false;
				}
				AdvancedCrafting.getAlloyManager().nameAlloy(p, args[1]);
				return true;
			}
		}
		return false;
	}
}
