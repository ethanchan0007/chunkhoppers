package net.chunkhopper.src.listener;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import net.chunkhopper.src.commands.Retrochips;
import net.chunkhopper.src.gui.GUIManager;
import net.chunkhopper.src.objects.ChunkHopper;
import net.chunkhopper.src.utils.DataHandler;
import net.chunkhopper.src.utils.UMaterial;
import net.chunkhopper.src.Main;
import net.chunkhopper.src.nbt.NBT;
import net.chunkhopper.src.utils.ChatUtils;
import net.chunkhopper.src.utils.MiscUtils;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(block.getLocation());
            if (Retrochips.isRetrochip(item)) {

                if (chunkhopper.getLevel() < 3) {

                    change = true;
                    if (chunkhopper.getLevel() + 1 < 3) {

                        chunkhopper.setLevel(1 + chunkhopper.getLevel());
                        player.sendMessage(ChatUtils.chat("&c&l[!] &cUpgraded the chunkhopper to level " + chunkhopper.getLevel()));
                    } else {

                        chunkhopper.setLevel(3);
                        player.sendMessage(ChatUtils.chat("&cYou have maxed out the chunkhopper's level."));
                    }
                }
                if (change) {

                    item.setAmount(player.getItemInHand().getAmount() - 1);
                    if (item.getAmount() <= 0) player.setItemInHand(new ItemStack(Material.AIR));

                }
                this.dataHandler.getHoppers().remove(chunkhopper);
                this.dataHandler.getHoppers().add(chunkhopper);
                this.dataHandler.saveData();
            } else if (MiscUtils.getInstance().isUsedLocation(block.getLocation())) {
                event.setCancelled(true);
                GUIManager.getInstance().openMainInventory(player, chunkhopper.getLocation());
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (MiscUtils.getInstance().isUsedLocation(event.getInventory().getLocation())) {
            event.setCancelled(true);
            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(event.getInventory().getLocation());
            chunkhopper.setTransferring(true);
            chunkhopper.hopperTimer(this.dataHandler);
            event.getPlayer().openInventory(chunkhopper.getInventory(0));
        } else if (event.getInventory().getLocation() != null && event.getView().getTitle().equalsIgnoreCase(Main.name) && !MiscUtils.getInstance().isUsedLocation(event.getInventory().getLocation()))
        {
            event.setCancelled(true);
            ChunkHopper chunkhopper = (new ChunkHopper(event.getInventory().getLocation(), Bukkit.createInventory(null, 54, Main.name), MiscUtils.itemFilterList(), UUID.randomUUID().toString(), 1));
            dataHandler.getHoppers().add(chunkhopper);
            chunkhopper.setTransferring(true);
            chunkhopper.hopperTimer(this.dataHandler);
            event.getPlayer().openInventory(chunkhopper.getInventory(0));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            ItemStack item = event.getCurrentItem();
            NBT nbt = NBT.get(item);
            if (event.getView().getTitle()
                    .equals(ChatColor.translateAlternateColorCodes('&', "&8&nChunkHopper Information")) || event.getView().getTitle()
                    .equals(ChatColor.translateAlternateColorCodes('&', "&8&nChunkHopper Filter"))) {
                event.setCancelled(true);
                if (item.hasItemMeta())
                    if (item.getItemMeta().hasDisplayName()) {
                        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nChunkHopper Information"))) {
                            final Player player = (Player) event.getWhoClicked();
                            Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx").intValue(), nbt.getInt("locy").intValue(), nbt.getInt("locz").intValue());
                            if (item.getType() == UMaterial.CHEST.getMaterial()) {
                                ChunkHopper hopper = MiscUtils.getInstance().getHopperFromLocation(location);
                                GUIManager.getInstance().openItemFilterGUI(player, hopper, 0);
                            } else if (item.getType() == UMaterial.BARRIER.getMaterial()) {
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
                        } else if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nChunkHopper Filter"))) {
                            int page = nbt.getInt("page").intValue();
                            Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx").intValue(), nbt.getInt("locy").intValue(), nbt.getInt("locz").intValue());
                            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromChunk(location.getChunk());
                            if (item.getItemMeta().getDisplayName().equals(ChatUtils.chat("&6<< Previous Page"))) {

                                if (page > 0) {
                                    GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), chunkhopper, page - 1);
                                }
                            } else if (item.getItemMeta().getDisplayName().equals(ChatUtils.chat("&6Next Page >>"))) {

                                if (page < (GUIManager.getInstance()).amountOfInv - 1) {
                                    GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), chunkhopper, page + 1);
                                }
                            }
                        }
                    } else if (item.getItemMeta().hasLore() &&
                            event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nChunkHopper Filter"))) {
                        int page = nbt.getInt("page").intValue();
                        Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx").intValue(), nbt.getInt("locy").intValue(), nbt.getInt("locz").intValue());
                        ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(location);
                        LinkedHashMap<ItemStack, Boolean> filterList = chunkhopper.getItemFilterList();

                        if (item.getItemMeta().getLore().contains(ChatUtils.chat("&a&lEnabled"))) {
                            for (ItemStack f : MiscUtils.itemFilterList().keySet()) {
                                if (f.getType().equals(item.getType())) {
                                    filterList.remove(f);
                                    filterList.put(f, false);
                                    ItemMeta meta = f.getItemMeta();
                                    meta.setLore(null);
                                    f.setItemMeta(meta);
                                }
                            }

                            chunkhopper.setItemFilterList(filterList);
                            for (ChunkHopper r : this.dataHandler.getHoppers()) {
                                if (r.getLocation() == chunkhopper.getLocation()) r = chunkhopper;
                            }
                            this.dataHandler.saveData();
                            GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), chunkhopper, page);
                        } else if (item.getItemMeta().getLore().contains(ChatUtils.chat("&c&lDisabled"))) {
                            for (ItemStack j : MiscUtils.itemFilterList().keySet()) {
                                if (j.getType().equals(item.getType())) {
                                    filterList.remove(j);
                                    ItemMeta meta = j.getItemMeta();
                                    meta.setLore(null);
                                    j.setItemMeta(meta);
                                    filterList.put(j, true);
                                }
                            }

                            chunkhopper.setItemFilterList(filterList);
                            for (ChunkHopper r : this.dataHandler.getHoppers()) {
                                if (r.getLocation() == chunkhopper.getLocation()) r = chunkhopper;
                            }
                            this.dataHandler.saveData();
                            GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), chunkhopper, page);
                        }
                    }
            }
        } catch (NullPointerException e)
        {
            return;
        }
    }
}
