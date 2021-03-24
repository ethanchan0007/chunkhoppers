package net.chunkhopper.src.listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.chunkhopper.src.Main;
import net.chunkhopper.src.commands.Retrochips;
import net.chunkhopper.src.gui.GUIManager;
import net.chunkhopper.src.nbt.NBT;
import net.chunkhopper.src.objects.ChunkHopper;
import net.chunkhopper.src.utils.ChatUtils;
import net.chunkhopper.src.utils.DataHandler;
import net.chunkhopper.src.utils.MiscUtils;
import net.chunkhopper.src.utils.UMaterial;
import org.bukkit.*;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

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
        if (player == null || block == null || SuperiorSkyblockAPI.getPlayer(player).getIsland() == null || SuperiorSkyblockAPI.getIslandAt(block.getLocation()) == null) return;
        if (!(player.isOp() || (SuperiorSkyblockAPI.getPlayer(player).getIsland().equals(SuperiorSkyblockAPI.getIslandAt(block.getLocation()))))) return;
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.isSneaking() && !player.getItemInHand().getType().equals(Material.HOPPER) && MiscUtils.getInstance().isUsedLocation(block.getLocation())) {
            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(block.getLocation());

            if (Retrochips.isRetrochip(item)) {

                if (chunkhopper.getLevel() < 3) {

                    change = true;
                    if (chunkhopper.getLevel() + 1 < 3) {

                        chunkhopper.setLevel(1 + chunkhopper.getLevel());
                        player.sendMessage(ChatUtils.chat("&c&l[!] &cUpgraded the retrohopper to level " + chunkhopper.getLevel()));
                    } else {

                        chunkhopper.setLevel(3);
                        player.sendMessage(ChatUtils.chat("&cYou have maxed out the retrohopper's level."));
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
            event.getPlayer().openInventory(chunkhopper.getInventories().get(0));
        } else if (event.getInventory().getLocation() != null && event.getView().getTitle().equalsIgnoreCase(Main.name) && !MiscUtils.getInstance().isUsedLocation(event.getInventory().getLocation())) {
            event.setCancelled(true);
            ChunkHopper chunkhopper = (new ChunkHopper(event.getInventory().getLocation(), MiscUtils.itemFilterList(), UUID.randomUUID().toString(), 1, 9));
            dataHandler.getHoppers().add(chunkhopper);
            chunkhopper.setTransferring(true);
            chunkhopper.hopperTimer(this.dataHandler);
            event.getPlayer().openInventory(chunkhopper.getInventories().get(0));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            ItemStack item = event.getCurrentItem();
            NBT nbt = NBT.get(item);
            if (event.getView().getTitle()
                    .equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetroHopper Information")) || event.getView().getTitle()
                    .equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetroHopper Filter")) || event.getView().getTitle()
                    .equals(ChatColor.translateAlternateColorCodes('&', "&8&nClick To Add Item To Filter"))) {
                event.setCancelled(true);
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasDisplayName()) {
                        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetroHopper Information"))) {
                            final Player player = (Player) event.getWhoClicked();
                            Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx").intValue(), nbt.getInt("locy").intValue(), nbt.getInt("locz").intValue());
                            ChunkHopper hopper = MiscUtils.getInstance().getHopperFromLocation(location);

                            if (item.getType() == UMaterial.CHEST.getMaterial()) {
                                GUIManager.getInstance().openItemFilterGUI(player, hopper, 0);
                            } else if (item.getType() == UMaterial.BARRIER.getMaterial()) {
                                MiscUtils.highlightChunk(location, player);
                            } else if (item.getType() == UMaterial.IRON_BLOCK.getMaterial()) {
                                if (!hopper.isCondensing()) {
                                    hopper.setCondensing(true);
                                    player.sendMessage(ChatUtils.chat("&aRetrohopper -> Condensing Ores"));
                                }
                                else {
                                    hopper.setCondensing(false);
                                    player.sendMessage(ChatUtils.chat("&cRetrohopper -> Not Condensing Ores"));
                                }

                                for (ChunkHopper r : this.dataHandler.getHoppers()) {
                                    if (r.getLocation().equals(hopper.getLocation())) {
                                        r.setItemFilterList(hopper.getItemFilterList());
                                    }
                                }
                                this.dataHandler.saveData();
                                GUIManager.getInstance().openMainInventory(player, hopper.getLocation());
                            } else if (item.getType() == UMaterial.REDSTONE.getMaterial()) {
                                if (!hopper.isParticleEnabled()) {
                                    hopper.setParticleEnabled(true);
                                    player.sendMessage(ChatUtils.chat("&aParticles Enabled!"));
                                }
                                else {
                                    hopper.setParticleEnabled(false);
                                    player.sendMessage(ChatUtils.chat("&cParticles Disabled!"));
                                }

                                for (ChunkHopper r : this.dataHandler.getHoppers()) {
                                    if (r.getLocation().equals(hopper.getLocation())) {
                                        r.setItemFilterList(hopper.getItemFilterList());
                                    }
                                }
                                this.dataHandler.saveData();
                                GUIManager.getInstance().openMainInventory(player, hopper.getLocation());
                            }
                        } else if (event.getView().getTopInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetroHopper Filter"))) {
                            int page = nbt.getInt("page").intValue();
                            Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx").intValue(), nbt.getInt("locy").intValue(), nbt.getInt("locz").intValue());
                            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromChunk(location.getChunk());
                            if (item.getItemMeta().getDisplayName().equals(ChatUtils.chat("&6<< Previous Page"))) {

                                if (page > 0) {
                                    GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), chunkhopper, page - 1);
                                }
                            } else if (item.getItemMeta().getDisplayName().equals(ChatUtils.chat("&6Next Page >>"))) {

                                if (page < chunkhopper.getAmountOfFilterPages() - 1) {
                                    GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), chunkhopper, page + 1);
                                }
                            } else if (item.getItemMeta().getDisplayName().contains(ChatUtils.chat("&aAdd Item"))) {
                                GUIManager.getInstance().openAddToFilterInv((Player) event.getWhoClicked(), chunkhopper);
                            }
                        }
                    } else if (item.getItemMeta().hasLore()) {
                        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nRetroHopper Filter"))) {
                            int page = nbt.getInt("page").intValue();
                            Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx").intValue(), nbt.getInt("locy").intValue(), nbt.getInt("locz").intValue());
                            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(location);
                            LinkedHashMap<ItemStack, Boolean> filterList = chunkhopper.getItemFilterList();

                            if (event.getClick().isRightClick() && event.getClick().isShiftClick() && item.getItemMeta().getLore().contains(ChatUtils.chat("&7Shift + Right Click to remove this item from the filter!"))) {
                                ItemStack itemStack = new ItemStack(item.getType(), 1, item.getData().getData());
                                filterList.remove(itemStack);
                                event.getWhoClicked().sendMessage(ChatUtils.chat("&4[!] &cRemoved " + itemStack.getType().name() + " from the item filter!"));
                            } else if (item.getItemMeta().getLore().contains(ChatUtils.chat("&a&lEnabled"))) {
                                for (ItemStack f : chunkhopper.getItemFilterList().keySet()) {
                                    if (f.getType().equals(item.getType()) && f.getData().getData() == item.getData().getData()) {
                                        filterList.replace(f, Boolean.valueOf(false));
                                        ItemMeta meta = f.getItemMeta();
                                        meta.setLore(null);
                                        f.setItemMeta(meta);
                                    }
                                }
                            } else if (item.getItemMeta().getLore().contains(ChatUtils.chat("&c&lDisabled"))) {
                                for (ItemStack j : chunkhopper.getItemFilterList().keySet()) {
                                    if (j.getType().equals(item.getType()) && j.getData().getData() == item.getData().getData()) {
                                        filterList.replace(j, Boolean.valueOf(true));
                                        ItemMeta meta = j.getItemMeta();
                                        meta.setLore(null);
                                        j.setItemMeta(meta);
                                    }
                                }
                            }
                            chunkhopper.setItemFilterList(filterList);
                            for (ChunkHopper r : this.dataHandler.getHoppers()) {
                                if (r.getLocation().equals(chunkhopper.getLocation())) {
                                    r.setItemFilterList(chunkhopper.getItemFilterList());
                                }
                            }
                            this.dataHandler.saveData();
                            GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), chunkhopper, page);
                        } else if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&8&nClick To Add Item To Filter"))) {
                            Location location = new Location(Bukkit.getWorld(nbt.getString("world")), nbt.getInt("locx").intValue(), nbt.getInt("locy").intValue(), nbt.getInt("locz").intValue());
                            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(location);
                            LinkedHashMap<ItemStack, Boolean> filterList = chunkhopper.getItemFilterList();

                            if (item.getItemMeta().getLore().contains(ChatUtils.chat("&3&l[!] &bClick to Add Item To the Filter!"))) {
                                filterList.put(new ItemStack(item.getType(), 1, item.getData().getData()), Boolean.valueOf(true));
                                event.getWhoClicked().sendMessage(ChatUtils.chat("&4&l[!] &c" + item.getType().name() + " has been added to the filter!"));
                                GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), chunkhopper, 0);
                            }

                            chunkhopper.setItemFilterList(filterList);
                            for (ChunkHopper r : this.dataHandler.getHoppers()) {
                                if (r.getLocation().equals(chunkhopper.getLocation())) {
                                    r.setItemFilterList(chunkhopper.getItemFilterList());
                                }
                            }
                            this.dataHandler.saveData();
                            GUIManager.getInstance().openItemFilterGUI((Player) event.getWhoClicked(), chunkhopper, 0);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            return;
        }
    }
}
