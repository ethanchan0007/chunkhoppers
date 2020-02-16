package net.retrohopper.src.gui;

import net.retrohopper.src.nbt.NBT;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.MiscUtils;
import net.retrohopper.src.utils.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class GUIManager {
    private static GUIManager instance = new GUIManager();
    public static GUIManager getInstance() {
        return instance;
    }

    public void openMainInventory(Player player, Location location) {
        Inventory gui = Bukkit.createInventory(player, 27,
                ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Information"));

        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        String coord = x + ", " + y + ", " + z;

        int multiplier = MiscUtils.getInstance().getHopperFromLocation(location).getMultiplier();
        List<String> upgradeLore;
        ItemStack hopperInv = MiscUtils.getInstance().getItemStack(UMaterial.CHEST.getMaterial(), 1, (byte) 0, ChatUtils.chat("&f&lFilter Options"), Arrays.asList(ChatUtils.chat("&7Change the item filter"), ChatUtils.chat("&7for this retrohopper")));

        ItemStack hopperStats = MiscUtils.getInstance().getItemStack(UMaterial.KNOWLEDGE_BOOK.getMaterial(), 1, (byte) 0, ChatUtils.chat("&f&lHopper Stats"), Arrays.asList(ChatUtils.chat("&7Hopper Location: &f&n" + coord), ChatUtils.chat("&7Hopper Level: &f&n" + MiscUtils.getInstance().getHopperFromLocation(location).getLevel()), ChatUtils.chat("&7Item Stacks transfered every second: &f&n" + multiplier)));

        ItemStack showChunkBorder = MiscUtils.getInstance().getItemStack(UMaterial.BARRIER.getMaterial(), 1, (byte) 0, ChatUtils.chat("&f&lShow Chunk Border"), Arrays.asList(ChatUtils.chat("&7Click this to show chunk borders for 5 seconds!")));


        gui.setItem(12, hopperInv);
        gui.setItem(14, hopperStats);
        gui.setItem(22, showChunkBorder);

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

    public void openItemFilterGUI(Player player, Retrohopper retrohopper) {
        Inventory gui = Bukkit.createInventory(player, 54,
                ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Filter"));

        int slot = 0;
        ItemStack it;
        LinkedHashMap<ItemStack, Boolean> filterList = (LinkedHashMap<ItemStack, Boolean>) retrohopper.getItemFilterList().clone();
        for (ItemStack item : filterList.keySet()) {
            it = UMaterial.match(item).getItemStack();
            ItemMeta meta = it.getItemMeta();
            if (filterList.get(item))
            {
                meta.setLore(Arrays.asList(ChatUtils.chat("&a&lEnabled")));
            } else
            {
                meta.setLore(Arrays.asList(ChatUtils.chat("&c&lDisabled")));
            }
            it.setItemMeta(meta);
            NBT nbt = NBT.get(it);
            nbt.setInt("locx", (int) retrohopper.getLocation().getX());
            nbt.setInt("locy", (int) retrohopper.getLocation().getY());
            nbt.setInt("locz", (int) retrohopper.getLocation().getZ());
            nbt.setString("world", retrohopper.getLocation().getWorld().getName());

            gui.setItem(slot, nbt.apply(it));
            slot++;

            meta.setLore(null);
            item.setItemMeta(meta);

        }

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
