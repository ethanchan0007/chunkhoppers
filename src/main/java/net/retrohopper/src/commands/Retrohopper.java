package net.retrohopper.src.commands;

import net.retrohopper.src.Main;
import net.retrohopper.src.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Retrohopper
        implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playername = "CONSOLE";
        int amount = 1;
        Player target = Bukkit.getPlayer(args[1]);
        if (sender instanceof Player) playername = sender.getName();
        if (args[0].equalsIgnoreCase("give") && (!(target == null))) {
            if (args.length == 3) {
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    amount = 1;
                }
            }
            target.getInventory().addItem(getHopperStack(amount));
            sender.sendMessage(
                    ChatUtils.chat("&3&l[!] &bYou gave " + target.getName() + " " + amount + "x &bretrohoppers!"));
            target.sendMessage(
                    ChatUtils.chat("&3&l[!] &b" + playername + " has given you " + amount + "x retrohoppers!"));
            return true;
        }
        return true;
    }

    public static ItemStack getHopperStack(int amount) {
        ItemStack arcHopper = new ItemStack(Material.HOPPER);
        ItemMeta meta = arcHopper.getItemMeta();
        meta.setDisplayName(Main.name);
        meta.setLore(Arrays.asList(ChatColor.WHITE + "This hopper will collect all items that", ChatColor.WHITE + "drop in its chunk!", "", ChatColor.WHITE + "It also stores " + ChatColor.AQUA + "54 stacks", ChatColor.WHITE + "and transfers " + ChatColor.AQUA + "2 stacks per 5 seconds"));
        arcHopper.setItemMeta(meta);
        arcHopper.setAmount(amount);
        return arcHopper;
    }
}

