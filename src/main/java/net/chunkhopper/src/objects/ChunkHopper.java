package net.chunkhopper.src.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.chunkhopper.src.Main;
import net.chunkhopper.src.utils.DataHandler;
import net.chunkhopper.src.utils.MiscUtils;
import net.chunkhopper.src.utils.WorldUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Hopper;
import org.bukkit.scheduler.BukkitRunnable;


public class ChunkHopper {
    private Location location;
    private Inventory[] inventoryList;
    private LinkedHashMap<ItemStack, Boolean> itemFilterList;
    private int level;
    private boolean isTransferring;
    String id;

    public ChunkHopper(Location loc, Inventory inventory, LinkedHashMap<ItemStack, Boolean> itemFilterList, String s, int level) {
        inventoryList = new Inventory[3];
        this.location = loc;
        this.itemFilterList = itemFilterList;
        this.id = s;
        this.level = level;
        this.isTransferring = false;
        inventoryList[0] = inventory;
    }

    public String toString() {
        return "Location: " + this.location + "\nInventory: " + this.inventoryList[0] + "\nItemFilterList: " + this.itemFilterList
                .toString() + "\nLevel: " + this.level + "\nID: " + this.id;
    }


    public boolean getTransferring() {
        return this.isTransferring;
    }


    public void setTransferring(boolean b) {
        this.isTransferring = b;
    }


    public void setLocation(Location location) {
        this.location = location;
    }


    public void setInventory(Inventory inventory, int num) {
        inventoryList[num] = inventory;
    }

    public void setInventoryList(Inventory[] inventoryList) {
        this.inventoryList = inventoryList;
    }


    public void setItemFilterList(LinkedHashMap<ItemStack, Boolean> itemFilterList) {
        this.itemFilterList = itemFilterList;
    }


    public Location getLocation() {
        return this.location;
    }


    public Chunk getChunk() {
        return this.location.getChunk();
    }


    public Inventory getInventory(int num) {
        return inventoryList[num];
    }

    public Inventory[] getInventoryList() {
        return inventoryList;
    }


    public LinkedHashMap<ItemStack, Boolean> getItemFilterList() {
        return this.itemFilterList;
    }


    public int getMultiplier() {
        return 9 * level;
    }


    public int getLevel() {
        return this.level;
    }


    public void setLevel(int level) {
        if (level >= 3) {
            this.level = 3;
        } else {
            this.level = level;
        }

    }

    public String getID() {
        return this.id;
    }


    public void setID(String id) {
        this.id = id;
    }

    public void addItemToChunkHopper(ItemStack itemStack)
    {
        for (Inventory inventory : inventoryList)
        {
            if (inventory.firstEmpty() != -1)
            {
                inventory.addItem(itemStack);
                return;
            }
        }
    }

    public void addItemToChunkHopper(ItemStack[] itemStack)
    {
        for (ItemStack item : itemStack) {
            for (Inventory inventory : inventoryList) {
                if (inventory.firstEmpty() != -1) {
                    inventory.addItem(itemStack);
                    return;
                }
            }
        }
    }

    public boolean isChunkHopperInventoryFull()
    {
        for (Inventory inventory : inventoryList)
        {
            if (inventory != null && !MiscUtils.isInventoryFull(inventory)) return false;
        }
        return true;
    }


    public void hopperTimer(final DataHandler dataHandler) {
        (new BukkitRunnable() {
            public void run() {
                for (Inventory inv : inventoryList) {
                    Location l = ChunkHopper.this.location;
                    if (inv != null) {
                        ItemStack[] contents = inv.getContents();
                        if (WorldUtils.isWorldLoaded(l.getWorld()) && l.getChunk().isLoaded()) {
                            try {
                                Hopper hopper = (Hopper) l.getBlock().getState().getData();
                                if (l.getBlock().getRelative(hopper.getFacing()).getState() instanceof InventoryHolder) {
                                    InventoryHolder ih = (InventoryHolder) l.getBlock().getRelative(hopper.getFacing()).getState();
                                    if (ih != null && ih.getInventory().firstEmpty() != -1 && !MiscUtils.isInventoryEmpty(inv.getContents())) {
                                        ArrayList<Integer> nonNull = new ArrayList<Integer>();
                                        for (int i = 0; i < inv.getContents().length; i++) {
                                            if (inv.getContents()[i] != null) nonNull.add(Integer.valueOf(i));
                                        }
                                        if (nonNull.size() > 0) {
                                            for (int i = 0; i < ChunkHopper.this.getMultiplier(); i++) {
                                                if (nonNull.size() > i) {
                                                    ih.getInventory().addItem(new ItemStack[]{contents[((Integer) nonNull.get(i)).intValue()]});
                                                    contents[((Integer) nonNull.get(i)).intValue()] = null;
                                                }
                                            }
                                            inv.setContents(contents);
                                        }
                                    } else {
                                        ChunkHopper.this.isTransferring = false;
                                        cancel();
                                    }
                                }
                            } catch (ClassCastException e) {
                                dataHandler.getHoppers().remove(MiscUtils.getInstance().getHopperFromLocation(ChunkHopper.this.location));
                                cancel();
                            }
                        } else {
                            ChunkHopper.this.isTransferring = false;
                            cancel();

                        }
                    } else {
                        ChunkHopper.this.isTransferring = false;
                        cancel();
                    }
                }
            }
        }).runTaskTimer(Main.getPlugin(), 20L, 20L);
    }

}
