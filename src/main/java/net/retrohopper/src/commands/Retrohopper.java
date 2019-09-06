package net.retrohopper.src.commands;

import java.util.Arrays;

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

public class Retrohopper
        implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if ((!(sender instanceof Player)) && (args.length != 2))
        {
            sender.sendMessage(ChatUtils.chat("&c[!] Only players may execute this command!"));
            return false;
        }
        if ((sender instanceof Player))
        {
            if (((Player)sender).getInventory().firstEmpty() != -1)
            {
                if (args.length != 1) {
                    ((Player)sender).getInventory().addItem(new ItemStack[] { getHopperStack(1) });
                    sender.sendMessage(ChatUtils.chat("&3&l[!] &bYou were given 1 retrohopper!"));
                } else {
                    ((Player)sender).getInventory().addItem(new ItemStack[] { getHopperStack(Integer.parseInt(args[0])) });
                    sender.sendMessage(ChatUtils.chat("&3&l[!] &bYou were given " + args[0] + " retrohoppers!"));
                }
            }
            else if (args.length != 1) {
                ((Player)sender).getWorld().dropItemNaturally(((Player)sender).getLocation(), getHopperStack(1));
            } else {
                ((Player)sender).getWorld().dropItemNaturally(((Player)sender).getLocation(), getHopperStack(Integer.parseInt(args[0])));
            }
        }
        else {
            Player toGive = Bukkit.getPlayer(args[1]);
            if (toGive.getInventory().firstEmpty() != -1) {
                toGive.getInventory().addItem(new ItemStack[] { getHopperStack(Integer.parseInt(args[0])) });
            } else {
                toGive.getWorld().dropItemNaturally(toGive.getLocation(), getHopperStack(Integer.parseInt(args[0])));
            }
        }
        return true;
    }

    public static ItemStack getHopperStack(int amount) {
        ItemStack arcHopper = new ItemStack(Material.HOPPER);
        ItemMeta meta = arcHopper.getItemMeta();
        meta.setDisplayName(Main.name);
        meta.setLore(Arrays.asList(new String[] { ChatColor.WHITE + "This hopper will collect all items that", ChatColor.WHITE + "drop in its chunk!", "", ChatColor.WHITE + "It also stores " + ChatColor.AQUA + "54 stacks", ChatColor.WHITE + "and transfers " + ChatColor.AQUA + "2 stacks per 5 seconds" }));
        arcHopper.setItemMeta(meta);
        arcHopper.setAmount(amount);
        return arcHopper;
    }
}

