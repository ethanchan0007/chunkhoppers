package net.retrohopper.src.gui;

import net.retrohopper.src.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GUIManager {
    private static GUIManager instance = new GUIManager();

    public static GUIManager getInstance()
    {
        return instance;
    }
    public void openMainInventory(Player player, Location location)
    {
        Inventory gui = Bukkit.createInventory(player, 27,
                ChatColor.translateAlternateColorCodes('&', "&3&lRetrohopper Controls"));

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String coord = x + "," + y + "," + z;

        ItemStack hopperInv = new ItemStack(Material.HOPPER, 1);
        ItemMeta hopperInvMeta = hopperInv.getItemMeta();
        hopperInvMeta.setDisplayName(ChatUtils.chat("&f&lOpen Hopper Inventory"));
        hopperInv.setItemMeta(hopperInvMeta);

        ItemStack hopperStats = new ItemStack(Material.BOOK, 1);
        ItemMeta hopperStatsMeta = hopperStats.getItemMeta();
        hopperStatsMeta.setDisplayName(ChatUtils.chat("&f&lHopper Stats"));
        hopperStatsMeta.setLore(Arrays.asList(ChatUtils.chat("&bHopper Location: " + coord), ChatUtils.chat("&bHopper Level: 1")));
        hopperStats.setItemMeta(hopperStatsMeta);

        gui.setItem(12, hopperInv);
        gui.setItem(14, hopperStats);

        ItemStack lightfillerItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 9);
        ItemMeta lightfillerItemMeta = lightfillerItem.getItemMeta();
        lightfillerItemMeta.setDisplayName(" ");
        lightfillerItem.setItemMeta(lightfillerItemMeta);

        ItemStack darkFillerItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
        ItemMeta darkFillerItemMeta = darkFillerItem.getItemMeta();
        darkFillerItemMeta.setDisplayName(" ");
        darkFillerItem.setItemMeta(darkFillerItemMeta);

        int i = 0;

        for (ItemStack itemStack : gui.getContents()) {
            if (itemStack == null)
                i++;
        }

        for (int j = 0; j < i; j++) {
            gui.setItem(gui.firstEmpty(), (gui.firstEmpty() % 2 == 1) ? lightfillerItem : darkFillerItem);
        }

        player.openInventory(gui);
        return;
    }
}
