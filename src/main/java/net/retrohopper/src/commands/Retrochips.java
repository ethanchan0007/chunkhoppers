package net.retrohopper.src.commands;

import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Retrochips implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playername = "CONSOLE";
        int amount = 0;
        if (!sender.hasPermission("retronixitems.admin")) {
            sender.sendMessage(ChatUtils.chat("&c&l[!] &cYou do not have permission to use this command!"));
            return true;
        }

        if (sender instanceof Player) playername = sender.getName();

        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[1]);
            amount = Integer.parseInt(args[2]);

            if (args[0].equalsIgnoreCase("give") && (!(target == null))) {
                ItemStack item = getRetrochip();
                item.setAmount(amount);
                    target.getInventory().addItem(item);
                    sender.sendMessage(
                            ChatUtils.chat("&3&l[!] &bYou gave " + target.getName() + " &ba retrochip!"));
                    target.sendMessage(
                            ChatUtils.chat("&3&l[!] &b" + playername + " has given you a retrochip!"));
            }


        } else
        {
            sender.sendMessage(ChatUtils.chat("&c&l[!] &cUsage: /retrochip give [player] [amount]"));
        } return true;
    }

    public ItemStack getRetrochip()
    {
        ItemStack item = UMaterial.PRISMARINE_CRYSTALS.getItemStack();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatUtils.chat("&3Retro&bchip"));
        item.setItemMeta(meta);

        return item;
    }

    public static boolean isRetrochip(ItemStack item)
    {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;
        if (item.getItemMeta().getDisplayName().equals(ChatUtils.chat("&3Retro&bchip")) && item.getType().equals(UMaterial.PRISMARINE_CRYSTALS.getMaterial())) return true;
        return false;
    }
}
