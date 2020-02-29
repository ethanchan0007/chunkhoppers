package net.retrohopper.src.listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;

import java.util.LinkedHashMap;
import java.util.List;

import net.retrohopper.src.Main;
import net.retrohopper.src.commands.Retrochips;
import net.retrohopper.src.gui.GUIManager;
import net.retrohopper.src.nbt.NBT;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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

public class HopperInteract
        implements Listener {
    public HopperInteract(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    private DataHandler dataHandler;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        ItemStack item = player.getItemInHand();
        boolean change = false;
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.isSneaking() && !player.getItemInHand().getType().equals(Material.HOPPER) && MiscUtils.getInstance().isUsedLocation(block.getLocation())) {
            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(block.getLocation());
            if (Retrochips.isRetrochip(item)) {

                if (retrohopper.getLevel() < 3) {

                    change = true;
                    if (retrohopper.getLevel() + 1 < 3) {

                        retrohopper.setLevel(1 + retrohopper.getLevel());
                        player.sendMessage(ChatUtils.chat("&c&l[!] &cUpgraded the retrohopper to level " + retrohopper.getLevel()));
                    } else {

                        retrohopper.setLevel(3);
                        player.sendMessage(ChatUtils.chat("&cYou have maxed out the retrohopper's level."));
                    }
                }
                if (change) {

                    item.setAmount(player.getItemInHand().getAmount() - 1);
                    if (item.getAmount() <= 0) player.setItemInHand(new ItemStack(Material.AIR));

                }
                this.dataHandler.getHoppers().remove(retrohopper);
                this.dataHandler.getHoppers().add(retrohopper);
                this.dataHandler.saveData();
            } else if (MiscUtils.getInstance().isUsedLocation(block.getLocation()) && ((SuperiorSkyblockAPI.getIslandAt(event.getClickedBlock().getLocation()) != null && SuperiorSkyblockAPI.getIslandAt(event.getClickedBlock().getLocation()).equals(SuperiorSkyblockAPI.getPlayer(event.getPlayer()).getIsland())) || event.getPlayer().isOp())) {
                event.setCancelled(true);
                GUIManager.getInstance().openMainInventory(player, retrohopper.getLocation());
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (MiscUtils.getInstance().isUsedLocation(event.getInventory().getLocation()) && ((SuperiorSkyblockAPI.getIslandAt(event.getInventory().getLocation()) != null && SuperiorSkyblockAPI.getIslandAt(event.getInventory().getLocation()).equals(SuperiorSkyblockAPI.getPlayer((Player) event.getPlayer()).getIsland())) || event.getPlayer().isOp())) {
            event.setCancelled(true);
            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(event.getInventory().getLocation());
            retrohopper.setTransferring(true);
            retrohopper.hopperTimer(this.dataHandler);
            event.getPlayer().openInventory(retrohopper.getInventory());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory().getTitle() == null || event.getCurrentItem() == null || event.getClickedInventory() == null || event.getClick() == ClickType.CREATIVE || event.getInventory() == null)
            return;

        ItemStack item = event.getCurrentItem();
        if (event.getClickedInventory().getTitle()
                .equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Information")) || event.getClickedInventory().getTitle()
                .equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Filter"))) {
            event.setCancelled(true);
            if (item.hasItemMeta())
                if (item.getItemMeta().hasDisplayName()) {
                    if (event.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Information"))) {
                        final Player player = (Player) event.getWhoClicked();
                        Inventory inventory = event.getClickedInventory();
                        ItemStack i = inventory.getItem(14);
                        List<String> lore = i.getItemMeta().getLore();
                        String[] locArray = ((String) lore.get(0)).split(": ")[1].split(", ");
                        Location location = new Location(player.getWorld(), Double.parseDouble(ChatColor.stripColor(locArray[0])), Double.parseDouble(ChatColor.stripColor(locArray[1])), Double.parseDouble(ChatColor.stripColor(locArray[2])));

                        if (item.getItemMeta().getDisplayName().equals(ChatUtils.chat("&f&lFilter Options"))) {
                            Retrohopper hopper = MiscUtils.getInstance().getHopperFromLocation(location);
                            GUIManager.getInstance().openItemFilterGUI(player, hopper, 0);
                        } else if (item.getItemMeta().getDisplayName().contains(ChatUtils.chat("&f&lShow Chunk Border"))) {
                            Chunk chunk = location.getChunk();
                            Location corner1 = chunk.getBlock(0, 0, 0).getLocation();
                            Location corner2 = chunk.getBlock(0, 0, 0).getLocation().add(16.0D, 255.0D, 16.0D);
                            List<Location> particleLocList = MiscUtils.getInstance().getHollowCube(corner1, corner2);
                            for (final Location particleLoc : particleLocList) {
                                (new BukkitRunnable() {
                                    int i = 0;

                                    public void run() {
                                        player.getWorld().spawnParticle(Particle.BARRIER, particleLoc, 0, 1.0D, 0.001D, 0.0D, 1.0D);
                                        if (this.i == 20) cancel();
                                        this.i++;
                                    }
                                }).runTaskTimer(Main.getPlugin(), 0L, 5L);
                            }
                        }
                    } else if (event.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Filter"))) {
                        NBT nbt = NBT.get(item);
                        int page = nbt.getInt("page").intValue();
                        Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx").intValue(), nbt.getInt("locy").intValue(), nbt.getInt("locz").intValue());
                        Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(location);
                        if (item.getItemMeta().getDisplayName().equals(ChatUtils.chat("&6<< Previous Page"))) {

                            if (page > 0) {
                                GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), retrohopper, page - 1);
                            }
                        } else if (item.getItemMeta().getDisplayName().equals(ChatUtils.chat("&6Next Page >>"))) {

                            if (page < (GUIManager.getInstance()).amountOfInv - 1) {
                                GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), retrohopper, page + 1);
                            }
                        }
                    }
                } else if (item.getItemMeta().hasLore() &&
                        event.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetrohopper Filter"))) {
                    NBT nbt = NBT.get(item);
                    int page = nbt.getInt("page").intValue();
                    Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx").intValue(), nbt.getInt("locy").intValue(), nbt.getInt("locz").intValue());
                    Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(location);
                    LinkedHashMap<ItemStack, Boolean> filterList = retrohopper.getItemFilterList();

                    if (item.getItemMeta().getLore().contains(ChatUtils.chat("&a&lEnabled"))) {
                        for (ItemStack f : filterList.keySet()) {
                            if (f.getType().equals(item.getType())) {
                                filterList.put(f, Boolean.valueOf(false));
                                ItemMeta meta = f.getItemMeta();
                                meta.setLore(null);
                                f.setItemMeta(meta);
                            }
                        }

                        retrohopper.setItemFilterList(filterList);
                        for (Retrohopper r : this.dataHandler.getHoppers()) {
                            if (r.getLocation() == retrohopper.getLocation()) r = retrohopper;
                        }
                        this.dataHandler.saveData();
                        GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), retrohopper, page);
                    } else if (item.getItemMeta().getLore().contains(ChatUtils.chat("&c&lDisabled"))) {
                        for (ItemStack j : filterList.keySet()) {
                            if (j.getType().equals(item.getType())) {
                                ItemMeta meta = j.getItemMeta();
                                meta.setLore(null);
                                j.setItemMeta(meta);
                                filterList.put(j, Boolean.valueOf(true));
                            }
                        }

                        retrohopper.setItemFilterList(filterList);
                        for (Retrohopper r : this.dataHandler.getHoppers()) {
                            if (r.getLocation() == retrohopper.getLocation()) r = retrohopper;
                        }
                        this.dataHandler.saveData();
                        GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), retrohopper, page);
                    }
                }
        }
    }
}
