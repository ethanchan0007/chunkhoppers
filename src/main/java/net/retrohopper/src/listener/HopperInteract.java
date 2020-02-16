package net.retrohopper.src.listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.retrohopper.src.Main;
import net.retrohopper.src.commands.Retrochips;
import net.retrohopper.src.gui.GUIManager;
import net.retrohopper.src.nbt.NBT;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class HopperInteract implements Listener {

    private DataHandler dataHandler;

    public HopperInteract(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        ItemStack item = player.getItemInHand();
        boolean change = false;
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.isSneaking() && !player.getItemInHand().getType().equals(Material.HOPPER) && MiscUtils.getInstance().isUsedLocation(block.getLocation())) {
            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(block.getLocation());
            if (Retrochips.isRetrochip(item))
            {
                if (!(retrohopper.getLevel() >= 3))
                {
                    change = true;
                    if (retrohopper.getLevel() + 1 < 3)
                    {
                        retrohopper.setLevel(1 + retrohopper.getLevel());
                        player.sendMessage(ChatUtils.chat("&c&l[!] &cUpgraded the retrohopper to level " + retrohopper.getLevel()));
                    }
                    else
                    {
                        retrohopper.setLevel(3);
                        player.sendMessage(ChatUtils.chat("&cYou have maxed out the retrohopper's level."));
                    }
                }
                if (change)
                {
                    item.setAmount(player.getItemInHand().getAmount() - 1);
                    if (item.getAmount() <= 0) player.setItemInHand(new ItemStack(Material.AIR));
                }

                dataHandler.getHoppers().remove(retrohopper);
                dataHandler.getHoppers().add(retrohopper);
                dataHandler.saveData();
            }
            else if (MiscUtils.getInstance().isUsedLocation(block.getLocation()) && ((SuperiorSkyblockAPI.getIslandAt(event.getClickedBlock().getLocation()) != null && SuperiorSkyblockAPI.getIslandAt(event.getClickedBlock().getLocation()).equals(SuperiorSkyblockAPI.getPlayer((Player) event.getPlayer()).getIsland()) )|| event.getPlayer().isOp())) {
                event.setCancelled(true);
                GUIManager.getInstance().openMainInventory(player, retrohopper.getLocation());
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (MiscUtils.getInstance().isUsedLocation(event.getInventory().getLocation()) && ((SuperiorSkyblockAPI.getIslandAt(event.getInventory().getLocation()) != null && SuperiorSkyblockAPI.getIslandAt(event.getInventory().getLocation()).equals(SuperiorSkyblockAPI.getPlayer((Player) event.getPlayer()).getIsland()) )|| event.getPlayer().isOp())) {
            event.setCancelled(true);
            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(event.getInventory().getLocation());
            event.getPlayer().openInventory(retrohopper.getInventory());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || event.getClickedInventory() == null) return;
        if (event.getClick() == ClickType.CREATIVE) return;
        if (event.getClickedInventory().getTitle() != null && event.getClickedInventory().getTitle()
                .equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Information")) || event.getClickedInventory().getTitle()
                .equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Filter"))) {
            event.setCancelled(true);
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasDisplayName()) {
                    final Player player = (Player) event.getWhoClicked();
                    Inventory inventory = event.getClickedInventory();
                    ItemStack i = inventory.getItem(14);
                    List<String> lore = i.getItemMeta().getLore();
                    String[] locArray = lore.get(0).split(": ")[1].split(", ");
                    Location location = new Location(player.getWorld(), Double.parseDouble(ChatColor.stripColor(locArray[0])), Double.parseDouble(ChatColor.stripColor(locArray[1])), Double.parseDouble(ChatColor.stripColor(locArray[2])));

                    if (item.getItemMeta().getDisplayName().equals(ChatUtils.chat("&f&lFilter Options"))) {
                        Retrohopper hopper = MiscUtils.getInstance().getHopperFromLocation(location);
                        GUIManager.getInstance().openItemFilterGUI(player, hopper);
                    } else if (item.getItemMeta().getDisplayName().contains(ChatUtils.chat("&f&lShow Chunk Border"))) {
                        Chunk chunk = location.getChunk();
                        Location corner1 = chunk.getBlock(0, 0, 0).getLocation();
                        Location corner2 = chunk.getBlock(0, 0, 0).getLocation().add(16, 255, 16);
                        List<Location> particleLocList = MiscUtils.getInstance().getHollowCube(corner1, corner2);
                        for (final Location particleLoc : particleLocList) {
                            new BukkitRunnable() {
                                int i = 0;

                                public void run() {
                                    player.getWorld().spawnParticle(Particle.BARRIER, particleLoc, 0, 1, 0.001, 0, 1);
                                    if (i == 20) cancel();
                                    i++;
                                }
                            }.runTaskTimer(Main.getPlugin(), 0, 5);
                        }
                    }
                } else if (item.getItemMeta().hasLore()) {
                    if (item.getItemMeta().getLore().contains(ChatUtils.chat("&a&lEnabled"))) {
                        NBT nbt = NBT.get(item);
                        Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx"), nbt.getInt("locy"), nbt.getInt("locz"));
                        Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(location);
                        LinkedHashMap<ItemStack, Boolean> filterList = retrohopper.getItemFilterList();
                        ItemMeta meta;
                        for (ItemStack f : filterList.keySet()) {
                            if (f.getType().equals(item.getType())) {
                                filterList.put(f, false);
                                meta = f.getItemMeta();
                                meta.setLore(null);
                                f.setItemMeta(meta);

                            }
                        }
                        retrohopper.setItemFilterList(filterList);
                        for (Retrohopper r : dataHandler.getHoppers()) {
                            if (r.getLocation() == retrohopper.getLocation()) r = retrohopper;
                        }
                        dataHandler.saveData();
                        GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), retrohopper);
                    } else if (item.getItemMeta().getLore().contains(ChatUtils.chat("&c&lDisabled"))) {
                        NBT nbt = NBT.get(item);
                        int page = nbt.getInt("page");
                        Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx"), nbt.getInt("locy"), nbt.getInt("locz"));
                        Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(location);
                        LinkedHashMap<ItemStack, Boolean> filterList = retrohopper.getItemFilterList();
                        ItemMeta meta;
                        for (ItemStack j : filterList.keySet()) {
                            if (j.getType().equals(item.getType())) {
                                meta = j.getItemMeta();
                                meta.setLore(null);
                                j.setItemMeta(meta);
                                filterList.put(j, true);

                            }
                        }
                        retrohopper.setItemFilterList(filterList);
                        for (Retrohopper r : dataHandler.getHoppers()) {
                            if (r.getLocation() == retrohopper.getLocation()) r = retrohopper;
                        }
                        dataHandler.saveData();
                        GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), retrohopper);
                    }
                }
            }
        }
    }
}
