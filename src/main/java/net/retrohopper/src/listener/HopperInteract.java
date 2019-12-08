package net.retrohopper.src.listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.sun.javafx.css.converters.BooleanConverter;
import net.retrohopper.src.Main;
import net.retrohopper.src.gui.GUIManager;
import net.retrohopper.src.nbt.NBT;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.List;

public class HopperInteract implements Listener {

    private DataHandler dataHandler;

    public HopperInteract(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = (Player) event.getPlayer();
        Block block = event.getClickedBlock();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.isSneaking() && !player.getItemInHand().getType().equals(Material.HOPPER)) {
            if (MiscUtils.getInstance().isUsedLocation(block.getLocation())) {
                event.setCancelled(true);
                Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(block.getLocation());
                GUIManager.getInstance().openMainInventory(player, retrohopper.getLocation());
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (MiscUtils.getInstance().isUsedLocation(event.getInventory().getLocation())) {
            event.setCancelled(true);
            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(event.getInventory().getLocation());
            event.getPlayer().openInventory(retrohopper.getInventory());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            if (event.getClickedInventory() != null && event.getCurrentItem() != null && event.getClickedInventory().getTitle() != null && event.getClickedInventory().getTitle()
                    .equals(ChatColor.translateAlternateColorCodes('&', "&3&lRetrohopper Information")) || event.getClickedInventory().getTitle()
                    .equals(ChatColor.translateAlternateColorCodes('&', "&3&lRetrohopper Filter"))) {
                event.setCancelled(true);
                if (event.getCurrentItem().hasItemMeta()) {
                    if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatUtils.chat("&f&lItems the hopper filters"))) {
                            Player player = (Player) event.getWhoClicked();
                            Inventory inventory = event.getClickedInventory();
                            ItemStack item = inventory.getItem(14);
                            List<String> lore = item.getItemMeta().getLore();
                            String[] locArray = lore.get(0).split(": ")[1].split(", ");
                            Location location = new Location(SuperiorSkyblockAPI.getIslandsWorld(), Double.parseDouble(ChatColor.stripColor(locArray[0])), Double.parseDouble(ChatColor.stripColor(locArray[1])), Double.parseDouble(ChatColor.stripColor(locArray[2])));
                            GUIManager.getInstance().openItemFilterGUI(player, MiscUtils.getInstance().getHopperFromLocation(location));
                        }
                    } else if (event.getCurrentItem().getItemMeta().hasLore())
                    {
                        if (event.getCurrentItem().getItemMeta().getLore().contains(ChatUtils.chat("&a&lEnabled"))) {
                            ItemStack i = event.getCurrentItem();
                            NBT nbt = NBT.get(i);
                            Location location = new Location(SuperiorSkyblockAPI.getIslandsWorld(), nbt.getInt("locx"), nbt.getInt("locy") , nbt.getInt("locz"));
                            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(location);
                            LinkedHashMap<ItemStack, Boolean> filterList = retrohopper.getItemFilterList();
                            ItemMeta meta;
                            for (ItemStack item : filterList.keySet())
                            {
                                if (item.getType().equals(i.getType()))
                                {
                                    filterList.put(item, false);
                                    meta = item.getItemMeta();
                                    meta.setLore(null);
                                    item.setItemMeta(meta);

                                }
                            }
                            retrohopper.setItemFilterList(filterList);
                            for (Retrohopper r : dataHandler.getHoppers())
                            {
                                if (r.getLocation() == retrohopper.getLocation()) r = retrohopper;
                            }
                            dataHandler.saveData();
                            GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), retrohopper);
                        } else if (event.getCurrentItem().getItemMeta().getLore().contains(ChatUtils.chat("&c&lDisabled"))) {
                            ItemStack i = event.getCurrentItem();
                            NBT nbt = NBT.get(i);
                            Location location = new Location(SuperiorSkyblockAPI.getIslandsWorld(), nbt.getInt("locx"), nbt.getInt("locy") , nbt.getInt("locz"));
                            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(location);
                            LinkedHashMap<ItemStack, Boolean> filterList = retrohopper.getItemFilterList();
                            ItemMeta meta;
                            for (ItemStack item : filterList.keySet())
                            {
                                if (item.getType().equals(i.getType()))
                                {
                                    meta = item.getItemMeta();
                                    meta.setLore(null);
                                    item.setItemMeta(meta);
                                    filterList.put(item, true);

                                }
                            }
                            retrohopper.setItemFilterList(filterList);
                            for (Retrohopper r : dataHandler.getHoppers())
                            {
                                if (r.getLocation() == retrohopper.getLocation()) r = retrohopper;
                            }
                            dataHandler.saveData();
                            GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), retrohopper);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}