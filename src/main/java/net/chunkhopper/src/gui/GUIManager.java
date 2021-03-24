package net.chunkhopper.src.gui;

import net.chunkhopper.src.Main;
import net.chunkhopper.src.config.ConfigData;
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
import org.bukkit.event.inventory.InventoryType;
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
        Inventory gui = Bukkit.createInventory(null, InventoryType.DISPENSER, ChatColor.translateAlternateColorCodes('&', "&8&nRetroHopper Information"));

        ChunkHopper hopper = MiscUtils.getInstance().getHopperFromLocation(location);

        int x = (int)location.getX();
        int y = (int)location.getY();
        int z = (int)location.getZ();
        String coord = x + ", " + y + ", " + z;

        int multiplier = hopper.getMultiplier();

        ItemStack hopperInv = MiscUtils.getInstance().getItemStack(UMaterial.CHEST.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lFilter Options"), Arrays.asList(new String[] { ChatUtils.chat("&7Change the item filter"), ChatUtils.chat("&7for this retrohopper") }));

        ItemStack hopperStats = MiscUtils.getInstance().getItemStack(UMaterial.KNOWLEDGE_BOOK.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lHopper Stats"), Arrays.asList(new String[] { ChatUtils.chat("&7Hopper Location: &f&n" + coord), ChatUtils.chat("&7Hopper Level: &f&n" + hopper.getLevel()), ChatUtils.chat("&7Item Stacks transfered every second: &f&n" + multiplier) }));

        ItemStack showChunkBorder = MiscUtils.getInstance().getItemStack(UMaterial.BARRIER.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lShow Chunk Border"), Arrays.asList(new String[] { ChatUtils.chat("&7Click this to show chunk borders for 5 seconds!") }));

        ItemStack condense = MiscUtils.getInstance().getItemStack(UMaterial.IRON_BLOCK.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lCondense Ores -> Blocks"), Arrays.asList(new String[] { ChatUtils.chat("&7The Condense feature is " + (hopper.isCondensing() ? "&aenabled" : "&cdisabled") + "&7!"), ChatUtils.chat("&7Click this to " + (!hopper.isCondensing() ? "&aenable" : "&cdisable") + " &7condensing!") }));

        ItemStack particles = MiscUtils.getInstance().getItemStack(UMaterial.REDSTONE.getMaterial(), 1, (byte)0, ChatUtils.chat("&f&lParticles on Transfer"), Arrays.asList(new String[] { ChatUtils.chat("&7This feature is " + (hopper.isParticleEnabled() ? "&aenabled" : "&cdisabled") + "&7!"), ChatUtils.chat("&7Click this to " + (!hopper.isParticleEnabled() ? "&aenable" : "&cdisable") + " &7particles on transfer!") }));

        hopperInv = addCoordNBT(hopperInv, location);
        showChunkBorder = addCoordNBT(showChunkBorder, location);
        condense = addCoordNBT(condense, location);
        particles = addCoordNBT(particles, location);

        gui.setItem(1, particles);
        gui.setItem(3, showChunkBorder);
        gui.setItem(4, hopperInv);
        gui.setItem(7, hopperStats);

        if (hopper.getLevel() >= 3)
            gui.setItem(5, condense);

        ItemStack fillerItem = ItemBuilder.getItemStack(UMaterial.BLUE_STAINED_GLASS_PANE.getItemStack(), " ");

        int i = 0;

        for (ItemStack itemStack : gui.getContents()) {
            if (itemStack == null) {
                i++;
            }
        }
        for (int j = 0; j < i; j++) {
            gui.setItem(gui.firstEmpty(),  fillerItem);
        }

        player.openInventory(gui);
    }

    public static List<Inventory> getDefaultChunkHopperInventories(ChunkHopper hopper, String uuid) {
        List<Inventory> inventoryList = new ArrayList<>();

        int amount = (hopper.getInvSize() / 45) + 1;

        for (int i = 0; i < amount; i++) {
            int size;
            if (i == amount - 1) {
                size = hopper.getInvSize() % 45;
            } else size = 45;

            Inventory inv = Bukkit.createInventory(null, 54, ChatUtils.chat(Main.name));

            ItemStack filler = ConfigData.fillerItems.get(0).clone();
            ItemMeta fillerMeta = filler.getItemMeta();
            if (fillerMeta.getLore() != null) {
                List<String> fillerLore = new ArrayList<>();
                for (String line : fillerMeta.getLore()) {
                    fillerLore.add(ChatUtils.chat(line.replaceAll("<page>", String.valueOf(i + 1))));
                }
                fillerMeta.setLore(fillerLore);
                filler.setItemMeta(fillerMeta);
            }
            NBT fillerNBT = NBT.get(filler);
            fillerNBT.setInt("page", i + 1);
            fillerNBT.setString("backpackUUID", uuid);
            filler = fillerNBT.apply(filler);

            ItemStack back = ConfigData.buttonItems.get(0).clone();
            ItemMeta backMeta = back.getItemMeta();
            if (backMeta.getLore() != null) {
                List<String> backLore = new ArrayList<>();
                for (String line : backMeta.getLore()) {
                    backLore.add(ChatUtils.chat(line.replaceAll("<page>", String.valueOf(i))));
                }
                backMeta.setLore(backLore);
                back.setItemMeta(backMeta);
            }
            NBT backNBT = NBT.get(back);
            backNBT.setInt("page", i + 1);
            backNBT.setString("backpackUUID", uuid);
            backNBT.setString("backpackButton", "back");
            back = backNBT.apply(back);

            ItemStack next = ConfigData.buttonItems.get(1).clone();
            ItemMeta nextMeta = next.getItemMeta();
            if (nextMeta.getLore() != null) {
                List<String> nextLore = new ArrayList<>();
                for (String line : nextMeta.getLore()) {
                    nextLore.add(ChatUtils.chat(line.replaceAll("<page>", String.valueOf(i + 2))));
                }
                nextMeta.setLore(nextLore);
                next.setItemMeta(nextMeta);
            }
            NBT nextNBT = NBT.get(next);
            nextNBT.setInt("page", i + 1);
            nextNBT.setString("backpackUUID", uuid);
            nextNBT.setString("backpackButton", "next");
            next = nextNBT.apply(next);

            if (!(i == 0 && i == amount - 1)) {
                if (i != amount - 1) {
                    inv.setItem(53, next);
                }
                if (i != 0) {
                    inv.setItem(45, back);
                }
            }
            for (int j = 0; j < 54; j++) {
                if (j >= size) {
                    if (inv.getContents()[j] == null) inv.setItem(j, filler);
                }
            }
            inventoryList.add(inv);
        }
        return inventoryList;
    }

    private ItemStack addCoordNBT(ItemStack itemStack, Location location)
    {
        NBT nbt = NBT.get(itemStack);
        nbt.setInt("locx", location.getBlockX());
        nbt.setInt("locy", location.getBlockY());
        nbt.setInt("locz", location.getBlockZ());
        nbt.setString("world", location.getWorld().getName());
        return nbt.apply(itemStack);
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
            previous = addCoordNBT(previous, chunkHopper.getLocation());

            ItemStack next = new ItemStack(UMaterial.PAPER.getMaterial(), 1);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatUtils.chat("&6Next Page >>"));
            next.setItemMeta(nextMeta);
            next = addCoordNBT(next, chunkHopper.getLocation());

            ItemStack addItem = new ItemStack(UMaterial.HOPPER.getMaterial(), 1);
            ItemMeta addItemMeta = next.getItemMeta();
            addItemMeta.setDisplayName(ChatUtils.chat("&aAdd Item"));
            addItem.setItemMeta(addItemMeta);
            addItem = addCoordNBT(addItem, chunkHopper.getLocation());

            gui.setItem(this.size, previous);
            gui.setItem(this.size + 4, addItem);
            gui.setItem(this.size + 8, next);

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
                itemStack = addCoordNBT(itemStack, chunkHopper.getLocation());

                (chunkHopper.getFilterInventories().get(slot / this.size)).setItem(slot % this.size, itemStack);
                slot++;
            }
        }
        player.openInventory(chunkHopper.getFilterInventories().get(page));
    }

    public void openAddToFilterInv(Player player, ChunkHopper chunkHopper)
    {

        Inventory gui = Bukkit.createInventory(player, this.size + 9,
                ChatColor.translateAlternateColorCodes('&', "&8&nClick To Add Item To Filter"));

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
                    itemStack = addCoordNBT(itemStack, chunkHopper.getLocation());
                    gui.setItem(slot, itemStack);
                    slot++;
                }
            }
        }

        player.openInventory(gui);
    }
}
