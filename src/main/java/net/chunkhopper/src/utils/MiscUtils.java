package net.chunkhopper.src.utils;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.chunkhopper.src.Main;
import net.chunkhopper.src.objects.ChunkHopper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class MiscUtils {
    public static MiscUtils instance = new MiscUtils();

    private DataHandler dataHandler = (Main.getPlugin()).dataHandler;


    public static MiscUtils getInstance() {
        return instance;
    }


    public static List<ItemStack> itemFilterMaterialList() {
        return Arrays.asList(UMaterial.COBBLESTONE.getItemStack(), UMaterial.STONE.getItemStack(), UMaterial.IRON_ORE.getItemStack(), UMaterial.IRON_INGOT.getItemStack(), UMaterial.SUGAR_CANE.getItemStack(), UMaterial.POPPY.getItemStack(), UMaterial.BLAZE_ROD.getItemStack());
    }


    public String getHopperLocationList() {
        String message = "";
        int count = 0;
        List<ChunkHopper> retrohopperList = this.dataHandler.getHoppers();
        for (ChunkHopper hopper : retrohopperList) {

            message = message + count + ") " + hopper + "\n";
            count++;
        }
        return message;
    }

    public static LinkedHashMap<ItemStack, Boolean> itemFilterList() {
        LinkedHashMap<ItemStack, Boolean> itemFilterList = new LinkedHashMap();
        for (ItemStack item : itemFilterMaterialList()) {
            itemFilterList.put(item, Boolean.valueOf(true));
        }
        return itemFilterList;
    }


    public boolean isInUsedChunk(Chunk chunk) {
        List<ChunkHopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return false;
        for (ChunkHopper hopper : retrohopperList) {
            if (hopper.getChunk() == chunk && hopper.getLocation() != null) return true;
        }
        return false;
    }

    public boolean isUsedLocation(Location location) {
        List<ChunkHopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return false;
        for (ChunkHopper hopper : retrohopperList) {
            if (hopper.getLocation() != null && hopper.getLocation().equals(location)) return true;
        }
        return false;
    }

    public ChunkHopper getHopperFromChunk(Chunk chunk) {
        List<ChunkHopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return null;
        for (ChunkHopper hopper : retrohopperList) {
            if (hopper.getLocation() != null && hopper.getChunk() != null && hopper.getChunk() == chunk) {
                return hopper;
            }
        }
        return null;
    }

    public Location getHopperLocationFromChunk(Chunk chunk) {
        List<ChunkHopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return null;
        for (ChunkHopper hopper : retrohopperList) {
            if (hopper.getChunk() == chunk && hopper.getLocation() != null) {
                return hopper.getLocation();
            }
        }
        return null;
    }

    public ChunkHopper getHopperFromLocation(Location location) {
        List<ChunkHopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return null;
        for (ChunkHopper hopper : retrohopperList) {
            if (hopper.getLocation().equals(location)) {
                return hopper;
            }
        }
        return null;
    }

    public ItemStack getItemStack(Material material, int amount, byte data, String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, amount, (short) data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static List<Location> getHollowCube(Location corner1, Location corner2) {
        List<Location> result = new ArrayList<Location>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());
        double x;
        for (x = minX; x <= maxX; x++) {
            double y;
            for (y = minY; y <= maxY; y++) {
                double z;
                for (z = minZ; z <= maxZ; z++) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }

        return result;
    }

    public static boolean areInventoriesEmpty(List<Inventory> inventories) {
        for (Inventory inventory : inventories) {
            if (inventory != null) {
                if (!isInventoryEmpty(inventory.getContents()))
                    return false;
            }

        }
        return true;
    }

    public static boolean isInventoryEmpty(ItemStack[] contents) {
        for (ItemStack item : contents) {
            if (item != null) return false;
        }
        return true;
    }

    public static boolean areInventoriesFull(List<Inventory> inventories) {
        for (Inventory inventory : inventories) {
            if (inventory != null) {
                for (ItemStack item : inventory.getContents()) {
                    if (item == null) return false;
                }
            }

        }
        return true;
    }

    public static boolean isInventoryFull(Inventory inventory) {
        for (ListIterator listIterator = inventory.iterator(); listIterator.hasNext(); ) {
            ItemStack item = (ItemStack) listIterator.next();
            if (item != null) {
                if (item.getAmount() != item.getMaxStackSize()) return false;
                continue;
            }
            return false;
        }

        return true;
    }

    public static boolean isPlayerInIslandWorlds(Player player) {
        return (player.getWorld().equals(SuperiorSkyblockAPI.getIslandsWorld()));
    }

    public static void highlightChunk(Location location, Player player) {
        highlightChunk(location.getChunk(), player);
    }

    public static void highlightChunk(Chunk chunk, Player player) {
        Location corner1 = chunk.getBlock(0, 0, 0).getLocation();
        Location corner2 = chunk.getBlock(0, 0, 0).getLocation().add(16.0D, 255.0D, 16.0D);
        List<Location> particleLocList = getHollowCube(corner1, corner2);
        for (final Location particleLoc : particleLocList) {
            (new BukkitRunnable() {
                int i = 0;

                public void run() {
                    player.spawnParticle(Particle.BARRIER, particleLoc, 0, 1.0D, 0.001D, 0.0D, 1.0D);
                    if (this.i == 20) cancel();
                    this.i++;
                }
            }).runTaskTimer(Main.getPlugin(), 0L, 5L);
        }
    }

    public static void drawParticleLine(Location start, Location end) {
        for (Location l : getLine(start, end))
            start.getWorld().spawnParticle(Particle.REDSTONE, l, 0);
    }

    public static List<Location> getLine(Location start, Location end) {
        List<Location> result = new ArrayList<Location>();
        Vector a = start.clone().toVector();
        Vector b = end.clone().toVector();
        Vector between = b.subtract(a);
        double length = between.length();
        between.normalize().multiply(0.1);
        double steps = length / 0.1;
        for (int i = 0; i < steps; i++) {
            result.add(a.add(between).toLocation(start.getWorld()));
        }

        return result;
    }

    public static ArrayList<ItemStack> condenseBlocks(List<ItemStack> og) {
        ArrayList<ItemStack> condensed = new ArrayList<>();
        HashMap<ItemStack, Integer> amountOfMaterialMap = new HashMap<>();

        for (ItemStack item : og) {
            if (item != null) {
                ItemStack clone = item.clone();
                clone.setAmount(1);
                if (amountOfMaterialMap.containsKey(clone))
                    amountOfMaterialMap.put(clone, item.getAmount() + amountOfMaterialMap.get(clone));
                else
                    amountOfMaterialMap.put(clone, item.getAmount());
            }
        }


        HashMap<ItemStack, ItemStack> mMap = getMaterialsThatCanBeCondensedList();
        for (ItemStack key : amountOfMaterialMap.keySet()) {
            boolean added = false;
            for (ItemStack i : mMap.keySet()) {
                if (key.getType() == i.getType() && key.getData().getData() == i.getData().getData()) {
                    added = true;
                    int amountOfBlocks = amountOfMaterialMap.get(key) / i.getAmount();
                    int remainder = amountOfMaterialMap.get(key) % i.getAmount();

                    ItemStack block = key.clone();
                    block.setType(mMap.get(i).getType());
                    List<ItemStack> blockList = formatItemsForList(block, amountOfBlocks);
                    condensed.addAll(blockList);

                    List<ItemStack> itemsList = formatItemsForList(key.clone(), remainder);
                    condensed.addAll(itemsList);
                }
            }
            if (!added) {
                List<ItemStack> itemsList = formatItemsForList(key, amountOfMaterialMap.get(key));
                condensed.addAll(itemsList);
            }
        }

        return condensed;
    }

    public static List<ItemStack> formatItemsForList(ItemStack itemStack, int amount) {
        List<ItemStack> itemsList = new ArrayList<>();
        for (int j = 0; j < (amount / 64) + 1; j++) {
            ItemStack i = itemStack.clone();
            if (j == amount / 64) i.setAmount(amount % 64);
            else i.setAmount(64);
            itemsList.add(i);
        }
        return itemsList;
    }

    public static HashMap<ItemStack, ItemStack> getMaterialsThatCanBeCondensedList() {
        HashMap<ItemStack, ItemStack> map = new HashMap<>();
        map.put(new ItemStack(Material.INK_SACK, 9, (short) 4), new ItemStack(Material.LAPIS_BLOCK));
        map.put(new ItemStack(Material.GOLD_NUGGET, 9, (short) 0), new ItemStack(Material.GOLD_INGOT));
        map.put(new ItemStack(Material.GOLD_INGOT, 9, (short) 0), new ItemStack(Material.GOLD_BLOCK));
        map.put(new ItemStack(Material.IRON_NUGGET, 9, (short) 0), new ItemStack(Material.IRON_INGOT));
        map.put(new ItemStack(Material.IRON_INGOT, 9, (short) 0), new ItemStack(Material.IRON_BLOCK));
        map.put(new ItemStack(Material.REDSTONE, 9, (short) 0), new ItemStack(Material.REDSTONE_BLOCK));
        map.put(new ItemStack(Material.DIAMOND, 9, (short) 0), new ItemStack(Material.DIAMOND_BLOCK));
        map.put(new ItemStack(Material.EMERALD, 9, (short) 0), new ItemStack(Material.EMERALD_BLOCK));

        return map;
    }

}
