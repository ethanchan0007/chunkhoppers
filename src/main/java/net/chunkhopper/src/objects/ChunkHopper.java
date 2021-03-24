package net.chunkhopper.src.objects;

import net.chunkhopper.src.Main;
import net.chunkhopper.src.gui.GUIManager;
import net.chunkhopper.src.nbt.NBT;
import net.chunkhopper.src.utils.DataHandler;
import net.chunkhopper.src.utils.MiscUtils;
import net.chunkhopper.src.utils.WorldUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Hopper;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;


public class ChunkHopper {
    String id;
    private Location location;
    private List<Inventory> inventories;
    private LinkedHashMap<ItemStack, Boolean> itemFilterList;
    private List<Inventory> filterInventories = new ArrayList();
    private int invSize;
    private int level;
    private boolean isTransferring;
    private boolean isCondensing = false;
    private boolean isParticleEnabled = true;

    public ChunkHopper(Location loc, LinkedHashMap<ItemStack, Boolean> itemFilterList, String s, int level, int invSize) {
        this.location = loc;
        this.itemFilterList = itemFilterList;
        this.id = s;
        this.level = level;
        this.isTransferring = false;
        this.invSize = invSize;
        this.inventories = (GUIManager.getDefaultChunkHopperInventories(this, id));

    }

    public int getInvSize() {
        return invSize;
    }

    public void setInvSize(int invSize) {
        this.invSize = invSize;
    }

    public boolean isCondensing() {
        return isCondensing;
    }

    public void setCondensing(boolean condensing) {
        isCondensing = condensing;
    }

    public boolean isParticleEnabled() {
        return isParticleEnabled;
    }

    public void setParticleEnabled(boolean particleEnabled) {
        this.isParticleEnabled = particleEnabled;
    }

    public String toString() {
        return "Location: " + this.location + "\nInventory: " + this.inventories + "\nItemFilterList: " + this.itemFilterList
                .toString() + "\nLevel: " + this.level + "\nID: " + this.id;
    }


    public boolean getTransferring() {
        return this.isTransferring;
    }


    public void setTransferring(boolean b) {
        this.isTransferring = b;
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public Chunk getChunk() {
        return this.location.getChunk();
    }

    public int getAmountOfFilterPages() {
        return (this.itemFilterList.keySet().size() / 45) + 1;
    }

    public List<Inventory> getFilterInventories() {
        return filterInventories;
    }

    public void setFilterInventories(List<Inventory> filterInventories) {
        this.filterInventories = filterInventories;
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }

    public LinkedHashMap<ItemStack, Boolean> getItemFilterList() {
        return this.itemFilterList;
    }

    public void setItemFilterList(LinkedHashMap<ItemStack, Boolean> itemFilterList) {
        this.itemFilterList = itemFilterList;
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

    public Location getCenterLocation() {
        return location.clone().add(0.5, 0.5, 0.5);
    }

    public String getID() {
        return this.id;
    }


    public void setID(String id) {
        this.id = id;
    }

    public boolean addItemToChunkHopper(ItemStack itemStack) {
        for (Inventory inventory : inventories) {
            if (inventory!= null) {
                for (ItemStack item : inventory.getContents()) {
                    if (item == null) {
                        inventory.addItem(itemStack);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isChunkHopperInventoryFull() {
        for (Inventory inv : inventories) {
            if (inv.firstEmpty() != -1)
                return false;
        }
        return true;
    }


    public void hopperTimer(final DataHandler dataHandler) {
        (new BukkitRunnable() {
            public void run() {
                Location l = ChunkHopper.this.location;
                int count = 0;
                for (Inventory inventory : inventories) {
                    if (inventory != null) {
                        List<ItemStack> contents = Arrays.asList(inventory.getContents());
                        if (WorldUtils.isWorldLoaded(l.getWorld()) && l.getChunk().isLoaded()) {
                            try {
                                Hopper hopper = (Hopper) l.getBlock().getState().getData();
                                Block block = l.getBlock().getRelative(hopper.getFacing()).getState().getBlock();
                                if (l.getBlock().getRelative(hopper.getFacing()).getState() instanceof InventoryHolder) {
                                    if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || block.getType() == Material.HOPPER) {
                                        InventoryHolder ih = (InventoryHolder) l.getBlock().getRelative(hopper.getFacing()).getState();
                                        if (ih != null && ih.getInventory().firstEmpty() != -1 && !MiscUtils.isInventoryEmpty(inventory.getContents())) {
                                            ArrayList<Integer> nonNull = new ArrayList<>();

                                            if (level == 3) {
                                                //if condense option enabled then convert to blocks
                                                if (isCondensing) {
                                                    contents = MiscUtils.condenseBlocks(contents);
                                                }
                                            }

                                            if (contents == null || contents.isEmpty()) {
                                                ChunkHopper.this.isTransferring = false;
                                                cancel();
                                            }

                                            for (int i = 0; i < contents.size(); i++) {
                                                if (contents.get(i) != null && !isPlaceHolder(contents.get(i))) nonNull.add(Integer.valueOf(i));
                                            }

                                            if (nonNull.size() > 0) {
                                                for (int i = 0; i < ChunkHopper.this.getMultiplier(); i++) {
                                                    if (nonNull.size() > i) {
                                                        ih.getInventory().addItem(new ItemStack[]{contents.get(((Integer) nonNull.get(i)).intValue())});
                                                        contents.set(nonNull.get(i).intValue(), null);
                                                    }
                                                }

                                                ItemStack[] items = new ItemStack[contents.size()];
                                                for (int i = 0; i < items.length; i++) {
                                                    items[i] = contents.get(i);
                                                }

                                                inventory.setContents(items);
                                            }
                                        } else {
                                            ChunkHopper.this.isTransferring = false;
                                            cancel();
                                        }
                                    } else if (block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE) {
                                        InventoryHolder ih = (InventoryHolder) l.getBlock().getRelative(hopper.getFacing()).getState();
                                        FurnaceInventory furnaceInventory = (FurnaceInventory) ih.getInventory();
                                        if (furnaceInventory != null && !MiscUtils.isInventoryEmpty(inventory.getContents()) && furnaceInventory.getResult() == null) {
                                            if (hopper.getFacing() == BlockFace.DOWN && furnaceInventory.getSmelting() == null) {
                                                ArrayList<Integer> nonNull = new ArrayList<Integer>();
                                                for (int i = 0; i < contents.size(); i++) {
                                                    if (contents.get(i) != null && !isPlaceHolder(contents.get(i))) nonNull.add(i);
                                                }

                                                if (nonNull.size() > 0) {
                                                    for (int i = 0; i < ChunkHopper.this.getMultiplier(); i++) {
                                                        if (nonNull.size() > i) {
                                                            ih.getInventory().addItem(new ItemStack[]{contents.get(((Integer) nonNull.get(i)).intValue())});
                                                            contents.set(nonNull.get(i), null);
                                                        }
                                                    }

                                                    ItemStack[] items = new ItemStack[contents.size()];
                                                    for (int i = 0; i < items.length; i++) {
                                                        items[i] = contents.get(i);
                                                    }

                                                    inventory.setContents(items);
                                                }
                                            } else if (hopper.getFacing() != BlockFace.DOWN && furnaceInventory.getFuel() == null) {
                                                ArrayList<Integer> nonNull = new ArrayList<Integer>();
                                                for (int i = 0; i < contents.size(); i++) {
                                                    if (contents.get(i) != null && !isPlaceHolder(contents.get(i))) nonNull.add(i);
                                                }

                                                if (nonNull.size() > 0) {
                                                    for (int i = 0; i < ChunkHopper.this.getMultiplier(); i++) {
                                                        if (nonNull.size() > i) {
                                                            ih.getInventory().addItem(new ItemStack[]{contents.get(((Integer) nonNull.get(i)).intValue())});
                                                            contents.set(nonNull.get(i).intValue(), null);
                                                        }
                                                    }

                                                    ItemStack[] items = new ItemStack[contents.size()];
                                                    for (int i = 0; i < items.length; i++) {
                                                        items[i] = contents.get(i);
                                                    }

                                                    inventory.setContents(items);
                                                }
                                            }
                                        } else {
                                            ChunkHopper.this.isTransferring = false;
                                            cancel();
                                        }
                                    }
                                    count++;
                                    if (count == 3600) {
                                        ChunkHopper.this.isTransferring = false;
                                        cancel();
                                    }
                                }
                            } catch (ClassCastException e) {
                                System.out.println(l);
                                e.printStackTrace();
                                dataHandler.getHoppers().remove(MiscUtils.getInstance().getHopperFromLocation(ChunkHopper.this.location));
                                cancel();
                            }
                        } else {
                            ChunkHopper.this.isTransferring = false;
                            cancel();

                        }
                    }
                }
            }
        }).runTaskTimerAsynchronously(Main.getPlugin(), 20L, 20L);
    }

    private boolean isPlaceHolder(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.getType() == Material.AIR || NBT.get(itemStack) == null)
            return true;
        NBT nbt = NBT.get(itemStack);
        return nbt.getString("placeholder") != null && !nbt.getString("placeholder").equals("");
    }

}
