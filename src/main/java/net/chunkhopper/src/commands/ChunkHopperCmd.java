package net.chunkhopper.src.commands;

import net.chunkhopper.src.Main;
import net.chunkhopper.src.nbt.NBT;
import net.chunkhopper.src.utils.ChatUtils;
import net.chunkhopper.src.utils.MiscUtils;
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
import java.util.List;

public class ChunkHopperCmd
        implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playername = "CONSOLE";
        int amount = 1, level = 1;
        if (args.length > 1)
        {
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
                target.getInventory().addItem(getHopperStack(amount, level));
                sender.sendMessage(
                        ChatUtils.chat("&3&l[!] &bYou gave " + target.getName() + " " + amount + "x &bchunkhoppers!"));
                target.sendMessage(
                        ChatUtils.chat("&3&l[!] &b" + playername + " has given you " + amount + "x chunkhoppers!"));
                return true;
            } else
            {
                if (sender.hasPermission("retrohopper.give"))
                {
                    sender.sendMessage(ChatUtils.chat("&4&l[!] &c/retrohopper give [player] [amount]"));
                } else
                {
                    sender.sendMessage(ChatUtils.chat("&4&l[!] &cYou do not have permission to use this command!"));
                }
            }
        } else if (args.length == 1)
        {
            if (sender.hasPermission("retronix.admin") && args[0].equalsIgnoreCase("list"))
            {
                sender.sendMessage(MiscUtils.getInstance().getHopperLocationList());
            } else if (sender.hasPermission("retronix.admin") && args[0].equalsIgnoreCase("info"))
            {
                sender.sendMessage(MiscUtils.getInstance().getHopperFromChunk(((Player) sender).getLocation().getChunk()).toString());
            } else
            {
                sender.sendMessage(ChatUtils.chat("&4[!] &cYou cannot use this command!"));
            }
        } else
        {
            sender.sendMessage(ChatUtils.chat("&c&l[!] &cUsage: /hopper give [player] [amount]"));
        }
        return true;
    }

    public static ItemStack getHopperStack(int amount, int level) {
        NBT nbt;

        ItemStack arcHopper = new ItemStack(Material.HOPPER);
        ItemMeta meta = arcHopper.getItemMeta();
        meta.setDisplayName(Main.name);
        meta.setLore(Arrays.asList(ChatUtils.chat("&fThis hopper will collect all items that"), ChatUtils.chat("&fdrop in its chunk!"), "", ChatColor.WHITE + "It also stores " + ChatColor.AQUA + "54 stacks", ChatColor.WHITE + "and transfers " + ChatColor.AQUA + (level*9) + " stacks per second"));
        arcHopper.setItemMeta(meta);
        arcHopper.setAmount(amount);

        nbt = NBT.get(arcHopper);
        nbt.setInt("Level", level);

        arcHopper = nbt.apply(arcHopper);

        return arcHopper;
    }



    public static boolean isRetrohopper(ItemStack item)
    {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore()) return false;
        List<String> lore = item.getItemMeta().getLore();
        for (String line : lore)
        {
            if (line.equals(ChatUtils.chat("&fThis hopper will collect all items that"))) return true;
        }
        return false;
    }
}

