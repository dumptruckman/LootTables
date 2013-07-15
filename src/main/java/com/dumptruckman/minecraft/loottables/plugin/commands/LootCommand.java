package com.dumptruckman.minecraft.loottables.plugin.commands;

import com.dumptruckman.minecraft.loottables.plugin.LootTable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LootCommand implements CommandExecutor {



    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1 || args.length > 2) {
                return false;
            }
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            if (args.length == 1 && player == null) {
                sender.sendMessage(ChatColor.DARK_RED + "You must be in game to send loot tables to your self or you must specify a recipient!");
                return false;
            }
            if (args.length == 2) {
                player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(ChatColor.DARK_RED + "'" + ChatColor.RED + args[1] + ChatColor.DARK_RED + "' does not seem to be online!");
                    return true;
                }
            }
        /*
            LootTable table = loot.getLootTable(args[0]);
            if (table == null) {
                sender.sendMessage(ChatColor.DARK_RED + "'" + ChatColor.RED + args[0] + ChatColor.DARK_RED + "' is not a valid loot table!");
                return true;
            }
            table.addToInventory(player.getInventory());
            sender.sendMessage(ChatColor.DARK_GREEN + "Gave loot table '" + ChatColor.GREEN + table.getName() + ChatColor.DARK_GREEN + "' to '" + ChatColor.GREEN + player.getName() + ChatColor.DARK_GREEN + "'!");
            */
        return true;
    }
}
