package net.chunkhopper.src.listener;

import net.chunkhopper.src.objects.ChunkHopper;
import net.chunkhopper.src.utils.DataHandler;
import net.chunkhopper.src.utils.MiscUtils;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
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

        try {
            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromChunk(chunk);
            ItemStack item = event.getEntity().getItemStack();
            int amount = item.getAmount();
            if (!chunkhopper.isChunkHopperInventoryFull() && !event.isCancelled()) {
                for (ItemStack i : chunkhopper.getItemFilterList().keySet()) {
                    if (i.getType() == item.getType() && chunkhopper.getItemFilterList().get(i) && i.getData().getData() == item.getData().getData()) {
                        if (!MiscUtils.areInventoriesFull(chunkhopper.getInventories())) {
                            chunkhopper.addItemToChunkHopper(item);
                        }
                        if (!chunkhopper.isParticleEnabled())
                            event.getEntity().getLocation().getWorld().playEffect(itemLoc, Effect.SMOKE, 1);
                        else
                            MiscUtils.drawParticleLine(event.getEntity().getLocation().clone(), chunkhopper.getCenterLocation().clone());
                        event.getEntity().remove();

                        if (!chunkhopper.getTransferring()) {
                            chunkhopper.hopperTimer(this.dataHandler);
                            chunkhopper.setTransferring(true);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    @EventHandler
    public void hopperPickup(InventoryPickupItemEvent event) {
        Location itemLoc = event.getInventory().getLocation();
        ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(itemLoc);


        if (chunkhopper != null) {

            ItemStack item = event.getItem().getItemStack();
            event.setCancelled(true);
            if (!chunkhopper.isChunkHopperInventoryFull()) {
                chunkhopper.addItemToChunkHopper(item);

                event.getItem().setItemStack(null);
                if (!chunkhopper.getTransferring()) {
                    chunkhopper.hopperTimer(this.dataHandler);
                    chunkhopper.setTransferring(true);
                }
            }
        }
    }


    @EventHandler
    public void hopperMoveEvent(InventoryMoveItemEvent event) {
        Location itemLoc = event.getDestination().getLocation();
        ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(itemLoc);
        if (chunkhopper != null) {
            ItemStack item = event.getItem();
            event.setCancelled(true);
            if (!chunkhopper.isChunkHopperInventoryFull()) {
                event.getInitiator().remove(item);
                item.setType(Material.AIR);
                if (!chunkhopper.getTransferring()) {
                    chunkhopper.hopperTimer(this.dataHandler);
                    chunkhopper.setTransferring(true);
                }
            }
        }
    }


    /*@EventHandler
    public void itemMerge(ItemStackEvent event) {
        Location itemLoc = event.getItem().getLocation();
        Chunk chunk = itemLoc.getChunk();

        if (MiscUtils.getInstance().getHopperFromLocation(itemLoc) != null) {
            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromChunk(chunk);

            ItemStack item = WildStackerAPI.getStackedItem(event.getItem().getItem()).getItemStack();
            int amount = WildStackerAPI.getItemAmount(event.getItem().getItem());
            ItemStack[] itemStacks = new ItemStack[(int) Math.ceil(amount / 64)];
            for (int j = 0; j < itemStacks.length; j++) {
                if (amount / 64 == 0)
                    item.setAmount(amount);
                else {
                    amount /= 64;
                    item.setAmount(64);
                }
                itemStacks[j] = item;
            }
            if (!chunkhopper.isChunkHopperInventoryFull() && !event.isCancelled()) {
                for (ItemStack i : chunkhopper.getItemFilterList().keySet()) {
                    if (i.getType() == item.getType() && ((Boolean) chunkhopper.getItemFilterList().get(i)).booleanValue()) {
                        chunkhopper.addItemToChunkHopper(itemStacks);
                        event.getItem().getLocation().getWorld().playEffect(itemLoc, Effect.SMOKE, 1);
                        event.getItem().remove();

                        if (!chunkhopper.getTransferring()) {
                            chunkhopper.hopperTimer(this.dataHandler);
                            chunkhopper.setTransferring(true);
                        }
                    }
                }
            }
        }
    }*/

}
