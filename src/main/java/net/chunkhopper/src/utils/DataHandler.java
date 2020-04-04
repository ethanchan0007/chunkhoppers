package net.chunkhopper.src.utils;

import net.chunkhopper.src.Main;
import net.chunkhopper.src.objects.ChunkHopper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataHandler {

    private File file = new File("plugins/ChunkHopper/", "data.yml");
    private FileConfiguration data = YamlConfiguration.loadConfiguration(file);
    private List<ChunkHopper> hoppers = new ArrayList<ChunkHopper>();
    private Map<UUID, ChunkHopper> upgrading = new HashMap<UUID, ChunkHopper>();

    public void saveData() {

        data.set("hoppers", new ArrayList<String>());
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hoppers == null || hoppers.isEmpty()) {
            return;
        }
        for (ChunkHopper hopper : hoppers) {
            double x = hopper.getLocation().getBlockX();
            double y = hopper.getLocation().getBlockY();
            double z = hopper.getLocation().getBlockZ();
            String world = hopper.getLocation().getWorld().getName();
            String hopperName = hopper.getID();
            data.set("hoppers." + hopperName + ".level", hopper.getLevel());
            data.set("hoppers." + hopperName + ".location.x", x);
            data.set("hoppers." + hopperName + ".location.y", y);
            data.set("hoppers." + hopperName + ".location.z", z);
            data.set("hoppers." + hopperName + ".location.world", world);

            // item filter
            for (ItemStack item : hopper.getItemFilterList().keySet())
            {
                data.set("hoppers." + hopperName + ".filter." + item.getType().toString(), hopper.getItemFilterList().get(item));
            }

        }
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void retrieveData() {
        if (data.getConfigurationSection("hoppers") == null) {
            return;
        }
        for (String s : data.getConfigurationSection("hoppers").getKeys(false)) {
            double x = data.getDouble("hoppers." + s + ".location.x");
            double y = data.getDouble("hoppers." + s + ".location.y");
            double z = data.getDouble("hoppers." + s + ".location.z");
            Inventory inventory = Bukkit.createInventory(null, 54, Main.name);
            LinkedHashMap<ItemStack, Boolean> itemFilterList = MiscUtils.itemFilterList(); // make it so that it imports custom one soon
            for (ItemStack item : itemFilterList.keySet())
            {
                itemFilterList.put(item, data.getBoolean("hoppers." + s + ".filter." + item.getType().toString()));
            }
            World world = Bukkit.getWorld(data.getString("hoppers." + s + ".location.world"));
            int level = data.getInt("hoppers." + s + ".level");
            Location location = new Location(world, x, y, z);
            ChunkHopper hopper = new ChunkHopper(location, inventory, itemFilterList, s, level);
            hoppers.add(hopper);
        }
    }

    public List<ChunkHopper> getHoppers() {
        return hoppers;
    }

    public void setHoppers(List<ChunkHopper> retrohopperList) {
        hoppers = retrohopperList;
    }

    public Map<UUID, ChunkHopper> getUpgrading() {
        return upgrading;
    }

}
