package net.retrohopper.src.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import net.retrohopper.src.Main;
import net.retrohopper.src.objects.Retrohopper;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MiscUtils {
    public static MiscUtils instance = new MiscUtils();

    private DataHandler dataHandler = (Main.getPlugin()).dataHandler;


    public static MiscUtils getInstance() {
        return instance;
    }


    public static List<ItemStack> itemFilterMaterialList() {
        return Arrays.asList(new ItemStack[]{UMaterial.COBBLESTONE.getItemStack(), UMaterial.STONE.getItemStack(), UMaterial.COAL_ORE.getItemStack(), UMaterial.COAL.getItemStack(), UMaterial.COAL_BLOCK.getItemStack(), UMaterial.LAPIS_ORE.getItemStack(), UMaterial.LAPIS_LAZULI.getItemStack(), UMaterial.LAPIS_BLOCK.getItemStack(), UMaterial.IRON_ORE.getItemStack(), UMaterial.IRON_INGOT.getItemStack(), UMaterial.IRON_BLOCK.getItemStack(), UMaterial.GOLD_ORE.getItemStack(), UMaterial.GOLD_NUGGET.getItemStack(), UMaterial.GOLD_INGOT.getItemStack(), UMaterial.GOLD_BLOCK.getItemStack(), UMaterial.REDSTONE_ORE.getItemStack(), UMaterial.REDSTONE.getItemStack(), UMaterial.REDSTONE_BLOCK.getItemStack(), UMaterial.DIAMOND_ORE.getItemStack(), UMaterial.DIAMOND.getItemStack(), UMaterial.DIAMOND_BLOCK.getItemStack(), UMaterial.EMERALD_ORE.getItemStack(), UMaterial.EMERALD.getItemStack(), UMaterial.EMERALD_BLOCK.getItemStack(), UMaterial.WHEAT_SEEDS.getItemStack(), UMaterial.WHEAT.getItemStack(), UMaterial.CARROT_ITEM.getItemStack(), UMaterial.POTATO_ITEM.getItemStack(), UMaterial.SUGAR_CANE_ITEM.getItemStack(), UMaterial.CACTUS.getItemStack(), UMaterial.PUMPKIN_SEEDS.getItemStack(), UMaterial.PUMPKIN.getItemStack(), UMaterial.MELON_SEEDS.getItemStack(), UMaterial.MELON_SLICE.getItemStack(), UMaterial.NETHER_WART.getItemStack(), UMaterial.RABBIT.getItemStack(), UMaterial.COOKED_RABBIT.getItemStack(), UMaterial.RABBIT_FOOT.getItemStack(), UMaterial.CHICKEN.getItemStack(), UMaterial.COOKED_CHICKEN.getItemStack(), UMaterial.FEATHER.getItemStack(), UMaterial.PORKCHOP.getItemStack(), UMaterial.COOKED_PORKCHOP.getItemStack(), UMaterial.MUTTON.getItemStack(), UMaterial.COOKED_MUTTON.getItemStack(), UMaterial.WHITE_WOOL.getItemStack(), UMaterial.BEEF.getItemStack(), UMaterial.COOKED_BEEF.getItemStack(), UMaterial.LEATHER.getItemStack(), UMaterial.ROTTEN_FLESH.getItemStack(), UMaterial.BONE.getItemStack(), UMaterial.ARROW.getItemStack(), UMaterial.SPIDER_EYE.getItemStack(), UMaterial.STRING.getItemStack(), UMaterial.GUNPOWDER.getItemStack(), UMaterial.BLAZE_ROD.getItemStack(), UMaterial.ENDER_PEARL.getItemStack(), UMaterial.POPPY.getItemStack(), UMaterial.PRISMARINE_SHARD.getItemStack(), UMaterial.SPONGE.getItemStack(), UMaterial.TROPICAL_FISH.getItemStack(), UMaterial.COOKED_COD.getItemStack(), UMaterial.FIRE_CHARGE.getItemStack(), UMaterial.GHAST_TEAR.getItemStack()});
    }


    public String getHopperLocationList() {
        String message = "";
        int count = 0;
        List<Retrohopper> retrohopperList = this.dataHandler.getHoppers();
        for (Retrohopper hopper : retrohopperList) {

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

    public boolean isInUsedChunk(Chunk chunk) {
        List<Retrohopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return false;
        for (Retrohopper hopper : retrohopperList) {
            if (hopper.getChunk() == chunk && hopper.getLocation() != null) return true;
        }
        return false;
    }

    public boolean isUsedLocation(Location location) {
        List<Retrohopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return false;
        for (Retrohopper hopper : retrohopperList) {
            if (hopper.getLocation() != null && hopper.getLocation().equals(location)) return true;
        }
        return false;
    }

    public Retrohopper getHopperFromChunk(Chunk chunk) {
        List<Retrohopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return null;
        for (Retrohopper hopper : retrohopperList) {
            if (hopper.getChunk() == chunk && hopper.getLocation() != null) {
                return hopper;
            }
        }
        return null;
    }

    public Location getHopperLocationFromChunk(Chunk chunk) {
        List<Retrohopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return null;
        for (Retrohopper hopper : retrohopperList) {
            if (hopper.getChunk() == chunk && hopper.getLocation() != null) {
                return hopper.getLocation();
            }
        }
        return null;
    }

    public Retrohopper getHopperFromLocation(Location location) {
        List<Retrohopper> retrohopperList = this.dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return null;
        for (Retrohopper hopper : retrohopperList) {
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

    public List<Location> getHollowCube(Location corner1, Location corner2) {
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

    public static boolean isInventoryEmpty(ItemStack[] inventory) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                return false;
            }
        }
        return true;
    }
}
