package net.retrohopper.src.utils;

import net.retrohopper.src.Main;
import net.retrohopper.src.objects.Retrohopper;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MiscUtils {

    private DataHandler dataHandler;
    public static MiscUtils instance = new MiscUtils();

    public MiscUtils()
    {
        this.dataHandler = Main.getPlugin().dataHandler;
    }


    public static MiscUtils getInstance()
    {
        return instance;
    }

    public static List<ItemStack> itemFilterMaterialList() {
        List<ItemStack> itemList = Arrays.asList(UMaterial.COBBLESTONE.getItemStack(), UMaterial.STONE.getItemStack(), UMaterial.STONE_BRICKS.getItemStack(), UMaterial.COAL_ORE.getItemStack(), UMaterial.COAL.getItemStack(), UMaterial.LAPIS_ORE.getItemStack(), UMaterial.LAPIS_LAZULI.getItemStack(), UMaterial.IRON_ORE.getItemStack(), UMaterial.IRON_INGOT.getItemStack(), UMaterial.GOLD_ORE.getItemStack(), UMaterial.GOLD_NUGGET.getItemStack(), UMaterial.GOLD_INGOT.getItemStack(), UMaterial.REDSTONE_ORE.getItemStack(), UMaterial.REDSTONE.getItemStack(), UMaterial.DIAMOND_ORE.getItemStack(), UMaterial.DIAMOND.getItemStack(), UMaterial.EMERALD_ORE.getItemStack(), UMaterial.EMERALD.getItemStack(), UMaterial.WHEAT.getItemStack(), UMaterial.CARROT_ITEM.getItemStack(), UMaterial.POTATO_ITEM.getItemStack(), UMaterial.SUGAR_CANE_ITEM.getItemStack(), UMaterial.CACTUS.getItemStack(), UMaterial.PUMPKIN.getItemStack(), UMaterial.MELON_SLICE.getItemStack(), UMaterial.NETHER_WART.getItemStack(), UMaterial.RABBIT.getItemStack(), UMaterial.COOKED_RABBIT.getItemStack(), UMaterial.RABBIT_FOOT.getItemStack(), UMaterial.CHICKEN.getItemStack(), UMaterial.COOKED_CHICKEN.getItemStack(), UMaterial.FEATHER.getItemStack(), UMaterial.PORKCHOP.getItemStack(), UMaterial.COOKED_PORKCHOP.getItemStack(), UMaterial.MUTTON.getItemStack(), UMaterial.COOKED_MUTTON.getItemStack(), UMaterial.WHITE_WOOL.getItemStack(), UMaterial.BEEF.getItemStack(), UMaterial.COOKED_BEEF.getItemStack(), UMaterial.LEATHER.getItemStack(), UMaterial.ROTTEN_FLESH.getItemStack(), UMaterial.BONE.getItemStack(), UMaterial.ARROW.getItemStack(), UMaterial.SPIDER_EYE.getItemStack(), UMaterial.STRING.getItemStack(), UMaterial.GUNPOWDER.getItemStack(), UMaterial.BLAZE_ROD.getItemStack(), UMaterial.ENDER_PEARL.getItemStack(), UMaterial.POPPY.getItemStack());

        return itemList;
    }

    public static LinkedHashMap<ItemStack, Boolean> itemFilterList()
    {
        LinkedHashMap<ItemStack, Boolean> itemFilterList = new LinkedHashMap<ItemStack, Boolean>();
        for (ItemStack item : itemFilterMaterialList())
        {
            itemFilterList.put(item, true);
        }
        return itemFilterList;
    }

    public static boolean isInventoryFull(Inventory inventory) {
        for (ItemStack item : inventory) {
            if (item != null) {
                if (item.getAmount() != item.getMaxStackSize()) return false;
            }
            else return false;
        }
        return true;
    }

    public boolean isInUsedChunk(Chunk chunk)
    {
        List<Retrohopper> retrohopperList = dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return false;
        for (Retrohopper hopper :  retrohopperList)
        {
            if (hopper.getChunk() == chunk && hopper.getLocation() != null) return true;
        }
        return false;
    }

    public boolean isUsedLocation(Location location)
    {
        List<Retrohopper> retrohopperList = dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return false;
        for (Retrohopper hopper :  retrohopperList)
        {
            if (hopper.getLocation() != null && hopper.getLocation().equals(location)) return true;
        }
        return false;
    }

    public Retrohopper getHopperFromChunk(Chunk chunk)
    {
        List<Retrohopper> retrohopperList = dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return null;
        for (Retrohopper hopper :  retrohopperList)
        {
            if (hopper.getChunk() == chunk && hopper.getLocation() != null)
            {
                return hopper;
            }
        }
        return null;
    }

    public Location getHopperLocationFromChunk(Chunk chunk)
    {
        List<Retrohopper> retrohopperList = dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return null;
        for (Retrohopper hopper :  retrohopperList)
        {
            if (hopper.getChunk() == chunk && hopper.getLocation() != null)
            {
                return hopper.getLocation();
            }
        }
        return null;
    }

    public Retrohopper getHopperFromLocation(Location location)
    {
        List<Retrohopper> retrohopperList = dataHandler.getHoppers();
        if (retrohopperList.isEmpty()) return null;
        for (Retrohopper hopper :  retrohopperList)
        {
            if (hopper.getLocation() != null && hopper.getLocation().equals(location))
            {
                return hopper;
            }
        }
        return null;
    }

}
