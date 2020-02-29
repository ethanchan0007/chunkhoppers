package net.retrohopper.src.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.retrohopper.src.Main;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import net.retrohopper.src.utils.WorldUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Hopper;
import org.bukkit.scheduler.BukkitRunnable;


public class Retrohopper {
    private Location location;
    private Inventory inventory;
    private LinkedHashMap<ItemStack, Boolean> itemFilterList;
    private int level;
    private boolean isTransferring;
    String id;

    public Retrohopper(Location loc, Inventory inventory, LinkedHashMap<ItemStack, Boolean> itemFilterList, String s, int level) {
        this.location = loc;
        this.inventory = inventory;
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


    public Location getLocation() {
        return this.location;
    }


    public Chunk getChunk() {
        return this.location.getChunk();
    }


    public Inventory getInventory() {
        return this.inventory;
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


    public void hopperTimer(final DataHandler dataHandler) {
        (new BukkitRunnable() {
            public void run() {
                Inventory inv = Retrohopper.this.inventory;
                Location l = Retrohopper.this.location;
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
                                    for (int i = 0; i < Retrohopper.this.getMultiplier(); i++) {
                                        if (nonNull.size() > i) {
                                            ih.getInventory().addItem(new ItemStack[]{contents[((Integer) nonNull.get(i)).intValue()]});
                                            contents[((Integer) nonNull.get(i)).intValue()] = null;
                                        }
                                    }
                                    inv.setContents(contents);
                                }
                            } else {
                                Retrohopper.this.isTransferring = false;
                                cancel();
                            }
                        }
                        dataHandler.saveData();
                    } catch (ClassCastException e) {
                        dataHandler.getHoppers().remove(MiscUtils.getInstance().getHopperFromLocation(Retrohopper.this.location));
                        cancel();
                    }
                } else {
                    Retrohopper.this.isTransferring = false;
                    cancel();

                }

            }
        }).runTaskTimer(Main.getPlugin(), 20L, 20L);
    }
}
