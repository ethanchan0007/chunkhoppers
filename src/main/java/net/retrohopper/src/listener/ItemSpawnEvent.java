package net.retrohopper.src.listener;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.events.ItemStackEvent;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.*;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemSpawnEvent implements Listener {
    public ItemSpawnEvent(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    private DataHandler dataHandler;

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
                    if (i.getType() == item.getType() && ((Boolean) retrohopper.getItemFilterList().get(i)).booleanValue()) {
                        inventory.addItem(new ItemStack[]{event.getEntity().getItemStack()});
                        event.getEntity().getLocation().getWorld().playEffect(itemLoc, Effect.SMOKE, 1);
                        event.getEntity().remove();
                        retrohopper.setInventory(inventory);
                        if (!retrohopper.getTransferring()) {
                            retrohopper.hopperTimer(this.dataHandler);
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
        Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(itemLoc);


        if (retrohopper != null) {
            Inventory inventory = retrohopper.getInventory();
            ItemStack item = event.getItem().getItemStack();
            event.setCancelled(true);
            if (inventory != null && !MiscUtils.isInventoryFull(inventory)) {
                inventory.addItem(new ItemStack[]{item});
                retrohopper.setInventory(inventory);
                event.getItem().setItemStack(null);
                if (!retrohopper.getTransferring()) {
                    retrohopper.hopperTimer(this.dataHandler);
                    retrohopper.setTransferring(true);
                }
            }
        }
    }


    @EventHandler
    public void hopperMoveEvent(InventoryMoveItemEvent event) {
        Location itemLoc = event.getDestination().getLocation();
        Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(itemLoc);


        if (retrohopper != null) {
            Inventory inventory = retrohopper.getInventory();
            ItemStack item = event.getItem();
            event.setCancelled(true);
            if (inventory != null && !MiscUtils.isInventoryFull(inventory)) {
                event.getInitiator().remove(item);
                item.setType(null);
                retrohopper.setInventory(inventory);
                if (!retrohopper.getTransferring()) {
                    retrohopper.hopperTimer(this.dataHandler);
                    retrohopper.setTransferring(true);
                }
            }
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
                    if (i.getType() == item.getType() && ((Boolean) retrohopper.getItemFilterList().get(i)).booleanValue()) {
                        inventory.addItem(new ItemStack[]{event.getItem().getItemStack()});
                        event.getItem().getLocation().getWorld().playEffect(itemLoc, Effect.SMOKE, 1);
                        event.getItem().remove();
                        retrohopper.setInventory(inventory);
                        if (!retrohopper.getTransferring()) {
                            retrohopper.hopperTimer(this.dataHandler);
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
        return false;
    }
}
