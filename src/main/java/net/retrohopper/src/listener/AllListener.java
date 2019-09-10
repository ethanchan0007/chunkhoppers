package net.retrohopper.src.listener;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.events.IslandDeleteEvent;
import com.wasteofplastic.askyblock.events.IslandPreDeleteEvent;
import net.retrohopper.src.HopperManager;
import net.retrohopper.src.Main;
import net.retrohopper.src.commands.Retrohopper;
import net.retrohopper.src.gui.GUIManager;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.WorldUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllListener
        implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (event.getItemInHand().equals(Retrohopper.getHopperStack(event.getItemInHand().getAmount()))) {
            if (event.getBlock().getWorld() != ASkyBlockAPI.getInstance().getIslandWorld()) {
                player.sendMessage(ChatUtils.chat("&4&l[!] &cYou are only allowed to place retrohoppers on the main island world!"));
                event.setCancelled(true);
            } else if (!HopperManager.hasChunk(event.getBlockPlaced().getChunk())) {
                player.sendMessage(ChatUtils.chat("&3&l[!] &bYou placed a retrohopper!"));
                HopperManager.registerHopper(event.getBlockPlaced().getLocation());
            } else {
                player.sendMessage(ChatUtils.chat("&4&l[!] &cOnly one retrohopper is allowed per chunk!"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public static void onIslandDelete(IslandPreDeleteEvent event) {
        int i = event.getIsland().getMinProtectedX();
        int j = event.getIsland().getMinProtectedZ();
        byte b = 0;
        ArrayList<Chunk> arrayList = new ArrayList();

        for (int k = i; k <= i + event.getIsland().getProtectionSize(); k += 16) {
            for (int m = j; m <= j + event.getIsland().getProtectionSize(); m += 16) {
                if (event.getIsland().inIslandSpace(k, m)) {
                    Chunk chunk = event.getIsland().getCenter().getWorld().getBlockAt(k, b, m).getChunk();
                    if (!arrayList.contains(chunk)) {
                        arrayList.add(chunk);
                    }
                }
            }
        }
        for (Chunk chunk : arrayList) {
            if (HopperManager.hasChunk(chunk) && HopperManager.getLocationForChunk(chunk) != null) {
                HopperManager.deleteHopper(HopperManager.getLocationForChunk(chunk));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if ((event.getBlock().getType().equals(Material.HOPPER)) && (HopperManager.hasExactLocation(event.getBlock().getLocation()))) {
            ItemStack[] contents = HopperManager.getInventory(event.getBlock().getLocation().getChunk()).getContents();
            HopperManager.deleteHopper(event.getBlock().getLocation());
            player.sendMessage(ChatUtils.chat("&3&l[!] &bYou removed a retrohopper!"));
            event.setDropItems(false);
            if (player.getInventory().firstEmpty() != -1) player.getInventory().addItem(Retrohopper.getHopperStack(1));
            else
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Retrohopper.getHopperStack(1));
            for (ItemStack stack : contents) {
                if (stack != null) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (HopperManager.hasExactLocation(event.getInventory().getLocation())) {
            event.setCancelled(true);
            GUIManager.getInstance().openMainInventory((Player) event.getPlayer(), event.getInventory().getLocation());
            //event.getPlayer().openInventory(HopperManager.getInventory(event.getInventory().getLocation().getChunk()));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            if (event.getClickedInventory() != null && event.getClickedInventory().getTitle()
                    .equals(ChatColor.translateAlternateColorCodes('&', "&3&lRetrohopper Controls"))) {
                event.setCancelled(true);
                if (event.getCurrentItem().hasItemMeta()) {
                    if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatUtils.chat("&f&lOpen Hopper Inventory"))) {
                            Player player = (Player) event.getWhoClicked();
                            if (event.isLeftClick()) {
                                String locString = event.getClickedInventory().getItem(14).getItemMeta().getLore().get(0).split(": ")[1];
                                String[] loclist = locString.split(",");
                                Location loc = new Location(ASkyBlockAPI.getInstance().getIslandWorld(), Double.parseDouble(loclist[0]),
                                        Double.parseDouble(loclist[1]), Double.parseDouble(loclist[2]));

                                event.getWhoClicked().openInventory(HopperManager.getInventory(loc.getChunk()));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void itemCreate(ItemSpawnEvent event) {
        Location itemLoc = event.getEntity().getLocation();
        Chunk chunk = itemLoc.getChunk();

        if (WorldUtils.isWorldLoaded(itemLoc.getWorld()) && chunk != null) ;
        {
            Location location = HopperManager.getLocationForChunk(chunk);
            Inventory inventory = HopperManager.getInventory(chunk);

            if ((inventory != null) && (inventory.firstEmpty() != -1) && (!event.isCancelled()) && (!event.getEntity().isDead())) {
                inventory.addItem(event.getEntity().getItemStack());
                event.getEntity().remove();
                HopperManager.setInventory(location, inventory);
            }
        }
    }
}