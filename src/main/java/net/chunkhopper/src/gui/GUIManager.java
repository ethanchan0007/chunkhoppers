package net.chunkhopper.src.gui;

import net.chunkhopper.src.nbt.NBT;
import net.chunkhopper.src.objects.ChunkHopper;
import net.chunkhopper.src.utils.ChatUtils;
import net.chunkhopper.src.utils.ItemBuilder;
import net.chunkhopper.src.utils.MiscUtils;
import net.chunkhopper.src.utils.UMaterial;
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
import java.util.List;

public class GUIManager
{
    private static GUIManager instance = new GUIManager();
    public int size = 45;
    public static GUIManager getInstance() { return instance; }


    public void openMainInventory(Player player, Location location) {
        Inventory gui = Bukkit.createInventory(player, 27,
                ChatColor.translateAlternateColorCodes('&', "&8&nRetroHopper Information"));

        int x = (int)location.getX();
        int y = (int)location.getY();
        int z = (int)location.getZ();
        String coord = x + ", " + y + ", " + z;

        int multiplier = MiscUtils.getInstance().getHopperFromLocation(location).getMultiplier();

        ItemStack hopperInv = MiscUtils.getInstance().getItemStack(UMaterial.CHEST.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lFilter Options"), Arrays.asList(new String[] { ChatUtils.chat("&7Change the item filter"), ChatUtils.chat("&7for this retrohopper") }));

        ItemStack hopperStats = MiscUtils.getInstance().getItemStack(UMaterial.KNOWLEDGE_BOOK.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lHopper Stats"), Arrays.asList(new String[] { ChatUtils.chat("&7Hopper Location: &f&n" + coord), ChatUtils.chat("&7Hopper Level: &f&n" + MiscUtils.getInstance().getHopperFromLocation(location).getLevel()), ChatUtils.chat("&7Item Stacks transfered every second: &f&n" + multiplier) }));

        ItemStack showChunkBorder = MiscUtils.getInstance().getItemStack(UMaterial.BARRIER.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lShow Chunk Border"), Arrays.asList(new String[] { ChatUtils.chat("&7Click this to show chunk borders for 5 seconds!") }));

        NBT nbt = NBT.get(hopperInv);
        nbt.setInt("locx", x);
        nbt.setInt("locy", y);
        nbt.setInt("locz", z);
        nbt.setString("world", location.getWorld().getName());

        NBT nbt1 = NBT.get(showChunkBorder);
        nbt1.setInt("locx", x);
        nbt1.setInt("locy", y);
        nbt1.setInt("locz", z);
        nbt1.setString("world", location.getWorld().getName());

        gui.setItem(12, nbt.apply(hopperInv));
        gui.setItem(14, hopperStats);
        gui.setItem(22, nbt1.apply(showChunkBorder));

        ItemStack lightfillerItem = ItemBuilder.getItemStack(UMaterial.GRAY_STAINED_GLASS_PANE.getItemStack(), " ");
        ItemStack darkFillerItem = ItemBuilder.getItemStack(UMaterial.BLACK_STAINED_GLASS_PANE.getItemStack(), " ");

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


    public void openItemFilterGUI(Player player, ChunkHopper chunkHopper, int page) {
        chunkHopper.setFilterInventories(new ArrayList<>());
        for (int o = 0; o < chunkHopper.getAmountOfFilterPages(); o++) {

            Inventory gui = Bukkit.createInventory(player, this.size + 9,
                    ChatColor.translateAlternateColorCodes('&', "&8&nRetroHopper Filter"));

            ItemStack previous = new ItemStack(Material.PAPER, 1);
            ItemMeta previousMeta = previous.getItemMeta();
            previousMeta.setDisplayName(ChatUtils.chat("&6<< Previous Page"));
            previous.setItemMeta(previousMeta);
            NBT nbt1 = NBT.get(previous);
            nbt1.setInt("locx", Integer.valueOf((int)chunkHopper.getLocation().getX()));
            nbt1.setInt("locy", Integer.valueOf((int)chunkHopper.getLocation().getY()));
            nbt1.setInt("locz", Integer.valueOf((int)chunkHopper.getLocation().getZ()));
            nbt1.setInt("page", Integer.valueOf(o));
            nbt1.setString("world", chunkHopper.getLocation().getWorld().getName());

            ItemStack next = new ItemStack(UMaterial.PAPER.getMaterial(), 1);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatUtils.chat("&6Next Page >>"));
            next.setItemMeta(nextMeta);
            NBT nbt2 = NBT.get(next);
            nbt2.setInt("locx", Integer.valueOf((int)chunkHopper.getLocation().getX()));
            nbt2.setInt("locy", Integer.valueOf((int)chunkHopper.getLocation().getY()));
            nbt2.setInt("locz", Integer.valueOf((int)chunkHopper.getLocation().getZ()));
            nbt2.setInt("page", Integer.valueOf(o));
            nbt2.setString("world", chunkHopper.getLocation().getWorld().getName());

            ItemStack addItem = new ItemStack(UMaterial.HOPPER.getMaterial(), 1);
            ItemMeta addItemMeta = next.getItemMeta();
            addItemMeta.setDisplayName(ChatUtils.chat("&aAdd Item"));
            addItem.setItemMeta(addItemMeta);
            NBT nbt3 = NBT.get(addItem);
            nbt3.setInt("locx", Integer.valueOf((int)chunkHopper.getLocation().getX()));
            nbt3.setInt("locy", Integer.valueOf((int)chunkHopper.getLocation().getY()));
            nbt3.setInt("locz", Integer.valueOf((int)chunkHopper.getLocation().getZ()));
            nbt3.setInt("page", Integer.valueOf(o));
            nbt3.setString("world", chunkHopper.getLocation().getWorld().getName());

            gui.setItem(this.size, nbt1.apply(previous));
            gui.setItem(this.size + 4, nbt3.apply(addItem));
            gui.setItem(this.size + 8, nbt2.apply(next));

            ItemStack lightfillerItem = ItemBuilder.getItemStack(UMaterial.GRAY_STAINED_GLASS_PANE.getItemStack(), " ");
            ItemStack darkFillerItem = ItemBuilder.getItemStack(UMaterial.BLACK_STAINED_GLASS_PANE.getItemStack(), " ");

            int i = 0;

            for (ItemStack itemStack : gui.getContents()) {
                if (itemStack == null) {
                    i++;
                }
            }
            for (int j = 0; j < i; j++) {
                gui.setItem(gui.firstEmpty(), (gui.firstEmpty() % 2 == 1) ? lightfillerItem : darkFillerItem);
            }
            chunkHopper.getFilterInventories().add(gui);
        }
        int slot = 0;

        if (!chunkHopper.getItemFilterList().isEmpty())
        {
            for (ItemStack item : chunkHopper.getItemFilterList().keySet())
            {
                ItemStack itemStack = new ItemStack(item.getType(), 1, item.getData().getData());
                ItemMeta meta = itemStack.getItemMeta();
                if ((chunkHopper.getItemFilterList().get(item)).booleanValue()) {

                    meta.setLore(Arrays.asList(new String[]{ChatUtils.chat("&a&lEnabled"), ChatUtils.chat("&7Shift + Right Click to remove this item from the filter!")}));
                } else {

                    meta.setLore(Arrays.asList(new String[]{ChatUtils.chat("&c&lDisabled"), ChatUtils.chat("&7Shift + Right Click to remove this item from the filter!")}));
                }
                itemStack.setItemMeta(meta);
                NBT nbt = NBT.get(itemStack);
                nbt.setInt("locx", Integer.valueOf((int) chunkHopper.getLocation().getX()));
                nbt.setInt("locy", Integer.valueOf((int) chunkHopper.getLocation().getY()));
                nbt.setInt("locz", Integer.valueOf((int) chunkHopper.getLocation().getZ()));
                nbt.setInt("page", Integer.valueOf(slot / this.size));
                nbt.setString("world", chunkHopper.getLocation().getWorld().getName());

                (chunkHopper.getFilterInventories().get(slot / this.size)).setItem(slot % this.size, nbt.apply(itemStack));
                slot++;
            }
        }
        player.openInventory(chunkHopper.getFilterInventories().get(page));
    }

    public void openAddToFilterInv(Player player, ChunkHopper chunkHopper)
    {

        Inventory gui = Bukkit.createInventory(player, this.size + 9,
                ChatColor.translateAlternateColorCodes('&', "&8&nClick To Add Item To Filter"));

        ItemStack lightfillerItem = ItemBuilder.getItemStack(UMaterial.GRAY_STAINED_GLASS_PANE.getItemStack(), " ");
        ItemStack darkFillerItem = ItemBuilder.getItemStack(UMaterial.BLACK_STAINED_GLASS_PANE.getItemStack(), " ");

        int i = 0;

        for (ItemStack itemStack : gui.getContents()) {
            if (itemStack == null) {
                i++;
            }
        }
        for (int j = 0; j < i; j++) {
            gui.setItem(gui.firstEmpty(), (gui.firstEmpty() % 2 == 1) ? lightfillerItem : darkFillerItem);
        }
        chunkHopper.getFilterInventories().add(gui);

        int slot = 0;
        List<ItemStack> usedItems = new ArrayList<>();

        for (ItemStack item : player.getInventory().getContents())
        {
            if(item != null) {
                ItemStack itemStack = new ItemStack(item.getType(), 1, item.getData().getData());
                if (!chunkHopper.getItemFilterList().containsKey(itemStack) && !usedItems.contains(itemStack)) {

                    usedItems.add(itemStack.clone());

                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setLore(Arrays.asList(new String[]{ChatUtils.chat("&3&l[!] &bClick to Add Item To the Filter!")}));
                    itemStack.setItemMeta(meta);

                    NBT nbt = NBT.get(itemStack);
                    nbt.setInt("locx", Integer.valueOf((int) chunkHopper.getLocation().getX()));
                    nbt.setInt("locy", Integer.valueOf((int) chunkHopper.getLocation().getY()));
                    nbt.setInt("locz", Integer.valueOf((int) chunkHopper.getLocation().getZ()));
                    nbt.setString("world", chunkHopper.getLocation().getWorld().getName());
                    gui.setItem(slot, nbt.apply(itemStack));
                    slot++;
                }
            }
        }

        player.openInventory(gui);
    }
}
