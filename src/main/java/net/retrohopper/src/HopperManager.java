package net.retrohopper.src;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public class HopperManager
{
    private static HashMap<Location, Inventory> hoppers = new HashMap();

    public static void registerHopper(Location l)
    {
        Main.logger.info("Adding hopper at " + l.toString());
        hoppers.put(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()), Bukkit.createInventory(null, 54, Main.name));
    }

    public static void deleteHopper(Location l)
    {
        Main.logger.info("Removing hopper at " + l.toString());
        hoppers.remove(l);
    }

    public static Location getLocationForChunk(Chunk chunk)
    {
        for (Location l : hoppers.keySet()) {
            if (l == null){}
            else if (l.getChunk().equals(chunk)) {
                return l;
            }

        }
        return null;
    }

    public static boolean hasChunk(Chunk chunk)
    {
        for (Location l : hoppers.keySet()) {
            if (l.getChunk().equals(chunk)) {
                return true;
            }
        }
        return false;
    }

    public static Inventory getInventory(Chunk chunk)
    {
        for (Location l : hoppers.keySet()) {
            if (l.getChunk().equals(chunk)) {
                return (Inventory)hoppers.get(l);
            }
        }
        return null;
    }

    public static void setInventory(Location l, Inventory inventory)
    {
        hoppers.put(l, inventory);
    }

    public static boolean hasExactLocation(Location exactLocation)
    {
        for (Location l : hoppers.keySet()) {
            if (l.equals(exactLocation)) {
                return true;
            }
        }
        return false;
    }

    public static HashMap<Location, Inventory> getHoppers()
    {
        return hoppers;
    }

    public static void setHoppers(HashMap<Location, Inventory> finalized)
    {
        hoppers = finalized;
    }
}

