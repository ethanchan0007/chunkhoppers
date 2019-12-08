package net.retrohopper.src.objects;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Retrohopper
{
    private Location location;
    private Inventory inventory;
    private LinkedHashMap<ItemStack, Boolean> itemFilterList;
    private int level;
    String id;

    public Retrohopper(Location loc, Inventory inventory, LinkedHashMap<ItemStack, Boolean> itemFilterList, String s, int level) {
        this.location = loc;
        this.inventory = inventory;
        this.itemFilterList = itemFilterList;
        this.id = s;
        this.level = level;
    }
    public String toString()
    {
        String message = "Location: " + location + "\n"
                + "Inventory: " + inventory + "\n"
                + "ItemFilterList: " + itemFilterList.toString() + "\n"
                + "Level: " + level + "\n"
                + "ID: " + id;
        return message;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setInventory(Inventory inventory)
    {
        this.inventory = inventory;
    }

    public void setItemFilterList(LinkedHashMap<ItemStack, Boolean> itemFilterList)
    {
        this.itemFilterList = itemFilterList;
    }

    public Location getLocation()
    {
       return this.location;
    }

    public Chunk getChunk()
    {
        return this.location.getChunk();
    }

    public Inventory getInventory()
    {
        return this.inventory;
    }

    public LinkedHashMap<ItemStack, Boolean> getItemFilterList()
    {
        return this.itemFilterList;
    }

    public int getMultiplier() {
        return ((level / 2) + 1);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }
}

