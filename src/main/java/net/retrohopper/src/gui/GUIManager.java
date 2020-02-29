package net.retrohopper.src.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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

public class GUIManager
{
    private static GUIManager instance = new GUIManager();
    private List<Inventory> filterInventories = new ArrayList();
    private int size = 18;
    public int amountOfInv = (int)Math.ceil(MiscUtils.itemFilterMaterialList().size() * 1.0D / this.size);

    public static GUIManager getInstance() { return instance; }


    public void openMainInventory(Player player, Location location) {
        Inventory gui = Bukkit.createInventory(player, 27,
                ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Information"));

        int x = (int)location.getX();
        int y = (int)location.getY();
        int z = (int)location.getZ();
        String coord = x + ", " + y + ", " + z;

        int multiplier = MiscUtils.getInstance().getHopperFromLocation(location).getMultiplier();

        ItemStack hopperInv = MiscUtils.getInstance().getItemStack(UMaterial.CHEST.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lFilter Options"), Arrays.asList(new String[] { ChatUtils.chat("&7Change the item filter"), ChatUtils.chat("&7for this retrohopper") }));

        ItemStack hopperStats = MiscUtils.getInstance().getItemStack(UMaterial.KNOWLEDGE_BOOK.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lHopper Stats"), Arrays.asList(new String[] { ChatUtils.chat("&7Hopper Location: &f&n" + coord), ChatUtils.chat("&7Hopper Level: &f&n" + MiscUtils.getInstance().getHopperFromLocation(location).getLevel()), ChatUtils.chat("&7Item Stacks transfered every second: &f&n" + multiplier) }));

        ItemStack showChunkBorder = MiscUtils.getInstance().getItemStack(UMaterial.BARRIER.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lShow Chunk Border"), Arrays.asList(new String[] { ChatUtils.chat("&7Click this to show chunk borders for 5 seconds!") }));


        gui.setItem(12, hopperInv);
        gui.setItem(14, hopperStats);
        gui.setItem(22, showChunkBorder);

        ItemStack lightfillerItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)9);
        ItemMeta lightfillerItemMeta = lightfillerItem.getItemMeta();
        lightfillerItemMeta.setDisplayName(" ");
        lightfillerItem.setItemMeta(lightfillerItemMeta);

        ItemStack darkFillerItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)11);
        ItemMeta darkFillerItemMeta = darkFillerItem.getItemMeta();
        darkFillerItemMeta.setDisplayName(" ");
        darkFillerItem.setItemMeta(darkFillerItemMeta);

        int i = 0;

        for (ItemStack itemStack : gui.getContents()) {
            if (itemStack == null) {
                i++;
            }
        }
        for (int j = 0; j < i; j++) {
            gui.setItem(gui.firstEmpty(), (gui.firstEmpty() % 2 == 1) ? lightfillerItem : darkFillerItem);
        }

        player.openInventory(gui);
    }


    public void openItemFilterGUI(Player player, Retrohopper retrohopper, int page) {
        for (int o = 0; o < this.amountOfInv; o++) {

            Inventory gui = Bukkit.createInventory(player, this.size + 9,
                    ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Filter"));

            ItemStack previous = new ItemStack(Material.PAPER, 1);
            ItemMeta previousMeta = previous.getItemMeta();
            previousMeta.setDisplayName(ChatUtils.chat("&6<< Previous Page"));
            previous.setItemMeta(previousMeta);
            NBT nbt1 = NBT.get(previous);
            nbt1.setInt("locx", Integer.valueOf((int)retrohopper.getLocation().getX()));
            nbt1.setInt("locy", Integer.valueOf((int)retrohopper.getLocation().getY()));
            nbt1.setInt("locz", Integer.valueOf((int)retrohopper.getLocation().getZ()));
            nbt1.setInt("page", Integer.valueOf(o));
            nbt1.setString("world", retrohopper.getLocation().getWorld().getName());

            ItemStack next = new ItemStack(Material.PAPER, 1);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatUtils.chat("&6Next Page >>"));
            next.setItemMeta(nextMeta);
            NBT nbt2 = NBT.get(next);
            nbt2.setInt("locx", Integer.valueOf((int)retrohopper.getLocation().getX()));
            nbt2.setInt("locy", Integer.valueOf((int)retrohopper.getLocation().getY()));
            nbt2.setInt("locz", Integer.valueOf((int)retrohopper.getLocation().getZ()));
            nbt2.setInt("page", Integer.valueOf(o));
            nbt2.setString("world", retrohopper.getLocation().getWorld().getName());

            gui.setItem(this.size, nbt1.apply(previous));
            gui.setItem(this.size + 8, nbt2.apply(next));

            ItemStack lightfillerItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)9);
            ItemMeta lightfillerItemMeta = lightfillerItem.getItemMeta();
            lightfillerItemMeta.setDisplayName(" ");
            lightfillerItem.setItemMeta(lightfillerItemMeta);

            ItemStack darkFillerItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)11);
            ItemMeta darkFillerItemMeta = darkFillerItem.getItemMeta();
            darkFillerItemMeta.setDisplayName(" ");
            darkFillerItem.setItemMeta(darkFillerItemMeta);

            int i = 0;

            for (ItemStack itemStack : gui.getContents()) {
                if (itemStack == null) {
                    i++;
                }
            }
            for (int j = 0; j < i; j++) {
                gui.setItem(gui.firstEmpty(), (gui.firstEmpty() % 2 == 1) ? lightfillerItem : darkFillerItem);
            }
            this.filterInventories.add(gui);
        }
        int slot = 0;

        LinkedHashMap<ItemStack, Boolean> filterList = (LinkedHashMap)retrohopper.getItemFilterList().clone();
        for (ItemStack item : filterList.keySet()) {
            ItemStack it = UMaterial.match(item).getItemStack();
            ItemMeta meta = it.getItemMeta();
            if ((filterList.get(item)).booleanValue()) {

                meta.setLore(Arrays.asList(new String[] { ChatUtils.chat("&a&lEnabled") }));
            } else {

                meta.setLore(Arrays.asList(new String[] { ChatUtils.chat("&c&lDisabled") }));
            }
            it.setItemMeta(meta);
            NBT nbt = NBT.get(it);
            nbt.setInt("locx", Integer.valueOf((int)retrohopper.getLocation().getX()));
            nbt.setInt("locy", Integer.valueOf((int)retrohopper.getLocation().getY()));
            nbt.setInt("locz", Integer.valueOf((int)retrohopper.getLocation().getZ()));
            nbt.setInt("page", Integer.valueOf(slot / this.size));
            nbt.setString("world", retrohopper.getLocation().getWorld().getName());

            (this.filterInventories.get(slot / this.size)).setItem(slot % this.size, nbt.apply(it));
            slot++;

            meta.setLore(null);
            item.setItemMeta(meta);
        }


        player.openInventory(this.filterInventories.get(page));
    }
}
