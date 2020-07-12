package net.chunkhopper.src.objects;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class ChunkHopper {
    private Location location;
    private Inventory inventory;
    private LinkedHashMap<ItemStack, Boolean> itemFilterList;
    private List<Inventory> filterInventories = new ArrayList();
    private int level;
    private boolean isTransferring;
    String id;

    public ChunkHopper(Location loc, Inventory inventory, LinkedHashMap<ItemStack, Boolean> itemFilterList, String s, int level) {
        this.inventory = inventory;
        this.location = loc;
        this.itemFilterList = itemFilterList;
        this.id = s;
        this.level = level;
        this.isTransferring = false;
    }

    public String toString() {
        return "Location: " + this.location + "\nInventory: " + this.inventory + "\nItemFilterList: " + this.itemFilterList
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


    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }


    public void setItemFilterList(LinkedHashMap<ItemStack, Boolean> itemFilterList) {
        this.itemFilterList = itemFilterList;
    }

    public void addItemToFilterList(ItemStack itemStack) {
        this.itemFilterList.put(itemStack, true);
    }

    public void addItemToFilterList(ItemStack itemStack, Boolean bool) {
        this.itemFilterList.put(itemStack, bool);
    }

    public void removeItemFromFilterList(ItemStack itemStack) {
        itemFilterList.remove(itemStack);
    }

    public Location getLocation() {
        return this.location;
    }


    public Chunk getChunk() {
        return this.location.getChunk();
    }

    public int getAmountOfFilterPages() {
        return (this.itemFilterList.keySet().size() / 45) + 1;
    }

    public List<Inventory> getFilterInventories()
    {
        return filterInventories;
    }

    public void setFilterInventories(List<Inventory> filterInventories)
    {
        this.filterInventories = filterInventories;
    }


    public Inventory getInventory() {
        return inventory;
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

    public void addItemToChunkHopper(ItemStack itemStack) {
        inventory.addItem(itemStack);
    }

    public boolean isChunkHopperInventoryFull() {
        return (inventory.firstEmpty() == -1);
    }


    public void hopperTimer(final DataHandler dataHandler) {
        (new BukkitRunnable() {
            public void run() {
                Location l = ChunkHopper.this.location;
                if (inventory != null) {
                    ItemStack[] contents = inventory.getContents();
                    if (WorldUtils.isWorldLoaded(l.getWorld()) && l.getChunk().isLoaded()) {
                        try {
                            Hopper hopper = (Hopper) l.getBlock().getState().getData();
                            if (l.getBlock().getRelative(hopper.getFacing()).getState() instanceof InventoryHolder) {
                                InventoryHolder ih = (InventoryHolder) l.getBlock().getRelative(hopper.getFacing()).getState();
                                if (ih != null && ih.getInventory().firstEmpty() != -1 && !MiscUtils.isInventoryEmpty(inventory.getContents())) {
                                    ArrayList<Integer> nonNull = new ArrayList<Integer>();
                                    for (int i = 0; i < inventory.getContents().length; i++) {
                                        if (inventory.getContents()[i] != null) nonNull.add(Integer.valueOf(i));
                                    }
                                    if (nonNull.size() > 0) {
                                        for (int i = 0; i < ChunkHopper.this.getMultiplier(); i++) {
                                            if (nonNull.size() > i) {
                                                ih.getInventory().addItem(new ItemStack[]{contents[((Integer) nonNull.get(i)).intValue()]});
                                                contents[((Integer) nonNull.get(i)).intValue()] = null;
                                            }
                                        }
                                        inventory.setContents(contents);
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
        }).runTaskTimer(Main.getPlugin(), 20L, 20L);
    }

}
