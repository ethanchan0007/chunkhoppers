package net.chunkhopper.src;

import net.chunkhopper.src.commands.Chunk;
import net.chunkhopper.src.commands.ChunkHopperCmd;
import net.chunkhopper.src.commands.Retrochips;
import net.chunkhopper.src.listener.BlockBreak;
import net.chunkhopper.src.listener.HopperInteract;
import net.chunkhopper.src.listener.HopperPlace;
import net.chunkhopper.src.listener.ItemSpawnEvent;
import net.chunkhopper.src.objects.ChunkHopper;
import net.chunkhopper.src.serializable.SerializableItemStack;
import net.chunkhopper.src.utils.ChatUtils;
import net.chunkhopper.src.utils.DataHandler;
import net.chunkhopper.src.utils.MiscUtils;
import net.chunkhopper.src.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static Main plugin;
    public static Logger logger;
    private TimerTask task;
    public static final String name = ChatUtils.chat("&8&nChunkHopper");
    public HashMap<String, SerializableItemStack[]> hoppers;
    public DataHandler dataHandler = new DataHandler();

    public void onEnable() {
        plugin = this;
        logger = getLogger();

        dataHandler.retrieveData();
        loadInventories();
        loadEvents();
        loadCommands();

        getLogger().info("Chunk Hopper enabled");
    }

    public void loadInventories() {
        int finalizedCount = 0;
        if (new File("plugins/ChunkHopper/chunkHoppers.ser").exists()) {
            ObjectInputStream ois;
            try {
                FileInputStream fis = new FileInputStream("plugins/ChunkHopper/chunkHoppers.ser");

                ois = new ObjectInputStream(fis);
                hoppers = (HashMap) ois.readObject();
                ois.close();
                fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            } catch (ClassNotFoundException c) {
                System.out.println("Class not found");
                c.printStackTrace();
                return;
            }
            for (String s : hoppers.keySet()) {
                String[] loc = s.split("~")[0].split(":");
                int num = 0;
                if (s.split("~").length == 2) num = Integer.parseInt(s.split("~")[1]);
                Inventory inventory = Bukkit.createInventory(null, 54, name);
                ItemStack[] stack = new ItemStack[hoppers.get(s).length];
                for (int i = 0; i < stack.length; i++) {
                    if (hoppers.get(s)[i] == null) stack[i] = null;
                    else stack[i] = hoppers.get(s)[i].toItemStack();
                }
                inventory.setContents(stack);

                

                Location location = new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
                if (MiscUtils.getInstance().isUsedLocation(location)) {
                    ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(location);
                    chunkhopper.setInventory(inventory, num);
                }
            }
            getLogger().info("Got " + dataHandler.getHoppers().size() + " hoppers!");
            getLogger().info("Successfully loaded (deserialized) chunkHoppers.ser");
        }
    }

    public void loadCommands() {
        getCommand("chunkhopper").setExecutor(new ChunkHopperCmd());
        getCommand("chunk").setExecutor(new Chunk());
        getCommand("retrochip").setExecutor(new Retrochips());
    }

    public void loadEvents() {
        getServer().getPluginManager().registerEvents(new BlockBreak(dataHandler), this);
        getServer().getPluginManager().registerEvents(new HopperInteract(dataHandler), this);
        getServer().getPluginManager().registerEvents(new HopperPlace(dataHandler), this);
        //getServer().getPluginManager().registerEvents(new IslandDelete(dataHandler), this);
        getServer().getPluginManager().registerEvents(new ItemSpawnEvent(dataHandler), this);

    }

    public void onDisable() {
        dataHandler.saveData();
        saveHopperContents();
        dataHandler = null;
        getLogger().info("Chunk Hopper disabled");
    }

    public void saveHopperContents() {
        try {
            HashMap<String, SerializableItemStack[]> hoppers = new HashMap();
            for (ChunkHopper chunkhopper : dataHandler.getHoppers()) {
                Location loc = chunkhopper.getLocation();
                for (int j = 0; j < chunkhopper.getInventoryList().length; j++) {
                    if (chunkhopper.getInventory(j) != null) {
                        SerializableItemStack[] stacks = new SerializableItemStack[chunkhopper.getInventory(j).getContents().length];
                        for (int i = 0; i < chunkhopper.getInventory(j).getContents().length; i++) {
                            if (chunkhopper.getInventory(j).getContents()[i] == null) {
                                stacks[i] = null;
                            } else {
                                stacks[i] = new SerializableItemStack(chunkhopper.getInventory(j).getContents()[i]);
                            }
                        }
                        hoppers.put(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + "~" + j, stacks);
                    } else
                    {
                        SerializableItemStack[] stacks = new SerializableItemStack[chunkhopper.getInventory(0).getContents().length];
                        hoppers.put(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + "~" + j, stacks);
                    }
                }
            }
            FileOutputStream fos = new FileOutputStream("plugins/ChunkHopper/chunkHoppers.ser");

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hoppers);
            oos.close();
            fos.close();
            getLogger().info("Successfully saved (serialized) hoppers to chunkHoppers.ser");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static Main getPlugin() {
        return plugin;
    }
}