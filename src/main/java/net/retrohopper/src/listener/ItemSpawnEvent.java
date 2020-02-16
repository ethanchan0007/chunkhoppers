package net.retrohopper.src.listener;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.events.ItemStackEvent;
import net.retrohopper.src.Main;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.*;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Hopper;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ItemSpawnEvent implements Listener {

    private DataHandler dataHandler;

    public ItemSpawnEvent(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @EventHandler
    public void itemCreate(org.bukkit.event.entity.ItemSpawnEvent event) {
        Location itemLoc = event.getEntity().getLocation();
        Chunk chunk = itemLoc.getChunk();

        if (WorldUtils.isWorldLoaded(itemLoc.getWorld()) && chunk != null && MiscUtils.getInstance().getHopperFromChunk(chunk) != null) {
            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromChunk(chunk);
            Inventory inventory = retrohopper.getInventory();
            ItemStack item = WildStackerAPI.getStackedItem(event.getEntity()).getItemStack();
            if (inventory != null && !MiscUtils.isInventoryFull(inventory) && !event.isCancelled()) {
                for (ItemStack i : retrohopper.getItemFilterList().keySet()) {
                    if (i.getType() == item.getType() && retrohopper.getItemFilterList().get(i)) {
                        inventory.addItem(event.getEntity().getItemStack());
                        event.getEntity().getLocation().getWorld().playEffect(itemLoc, Effect.SMOKE, 1);
                        event.getEntity().remove();
                        retrohopper.setInventory(inventory);
                        if (!retrohopper.getTransferring()) {
                            hopperTimer(retrohopper);
                            retrohopper.setTransferring(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void hopperPickup(InventoryPickupItemEvent event) {
        Location itemLoc = event.getInventory().getLocation();

        if (MiscUtils.getInstance().getHopperFromLocation(itemLoc) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void itemMerge(ItemStackEvent event) {
        Location itemLoc = event.getItem().getLocation();
        Chunk chunk = itemLoc.getChunk();

        if (MiscUtils.getInstance().getHopperFromLocation(itemLoc) != null) {
            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromChunk(chunk);
            Inventory inventory = retrohopper.getInventory();
            ItemStack item = event.getItem().getItemStack();
            if (inventory != null && !MiscUtils.isInventoryFull(inventory) && !event.isCancelled()) {
                for (ItemStack i : retrohopper.getItemFilterList().keySet()) {
                    if ((i.getType() == item.getType() && retrohopper.getItemFilterList().get(i))) {
                        inventory.addItem(event.getItem().getItemStack());
                        event.getItem().getLocation().getWorld().playEffect(itemLoc, Effect.SMOKE, 1);
                        event.getItem().remove();
                        retrohopper.setInventory(inventory);
                        if (!retrohopper.getTransferring()) {
                            hopperTimer(retrohopper);
                            retrohopper.setTransferring(true);
                        }
                    }
                }
            }
        }
    }

    public static boolean isSpawnerShard(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore()) return false;
        if (item.getType().equals(UMaterial.PRISMARINE_SHARD.getMaterial()) || item.getItemMeta().getLore().contains(ChatUtils.chat("&3to create a spawner!")))
            return true;
        else return false;
    }

    public void hopperTimer(final Retrohopper retrohopper) {
        new BukkitRunnable() {

            public void run() {
                Inventory inv = retrohopper.getInventory();
                Location l = retrohopper.getLocation();
                ItemStack[] contents = inv.getContents();
                if (WorldUtils.isWorldLoaded(l.getWorld()) && l.getChunk().isLoaded()) {
                    try {
                        Hopper hopper = (Hopper) l.getBlock().getState().getData();
                        if ((l.getBlock().getRelative(hopper.getFacing()).getState() instanceof InventoryHolder)) {
                            InventoryHolder ih = (InventoryHolder) l.getBlock().getRelative(hopper.getFacing()).getState();
                            if (ih != null && !(ih.getInventory().firstEmpty() == -1) && !isInventoryEmpty(inv.getContents())) {
                                ArrayList<Integer> nonNull = new ArrayList();
                                for (int i = 0; i < inv.getContents().length; i++) {
                                    if (inv.getContents()[i] != null) nonNull.add(Integer.valueOf(i));
                                }
                                if (nonNull.size() > 0) {
                                    for (int i = 0; i < retrohopper.getMultiplier(); i++) {
                                        if (nonNull.size() > i) {
                                            ih.getInventory().addItem(contents[nonNull.get(i).intValue()]);
                                            contents[nonNull.get(i).intValue()] = null;
                                        }
                                    }
                                    inv.setContents(contents);
                                }
                            } else {
                                retrohopper.setTransferring(false);
                                cancel();
                            }
                        }
                        dataHandler.saveData();

                    } catch (ClassCastException e) {
                        dataHandler.getHoppers().remove(retrohopper);
                        cancel();
                    }
                } else {
                    retrohopper.setTransferring(false);
                    cancel();
                }


            }
        }.runTaskTimer(Main.getPlugin(), 20L, 20 * 1L);
    }

    public boolean isInventoryEmpty(ItemStack[] inventory) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                return false;
            }
        }
        return true;
    }
}
