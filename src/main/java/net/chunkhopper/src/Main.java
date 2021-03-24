package net.chunkhopper.src;

import net.chunkhopper.src.commands.Chunk;
import net.chunkhopper.src.commands.ChunkHopperCmd;
import net.chunkhopper.src.commands.Retrochips;
import net.chunkhopper.src.listener.*;
import net.chunkhopper.src.objects.ChunkHopper;
import net.chunkhopper.src.serializable.SerializableItemStack;
import net.chunkhopper.src.utils.ChatUtils;
import net.chunkhopper.src.utils.DataHandler;
import net.chunkhopper.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static Main plugin;
    public static Logger logger;
    private TimerTask task;
    public static final String name = ChatUtils.chat("&3&nRetroHopper");
    public HashMap<String, SerializableItemStack[]> hoppers;
    public DataHandler dataHandler = new DataHandler();

    public void onEnable() {
        plugin = this;

        logger = getLogger();
        new BukkitRunnable() {

            @Override
            public void run() {

                dataHandler.retrieveData();
                loadInventories();
                loadEvents();
                loadCommands();

                getLogger().info("Chunk Hopper enabled");
            }
        }.runTaskLater(this, 1l);
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
            for (String line : hoppers.keySet()) {
                List<Inventory> inventoryList = new ArrayList<>();
                // get backpack type of uuid
                String uuid = line.substring(0,36);
                int num = Integer.parseInt(line.substring(37));

                ChunkHopper chunkHopper = dataHandler.getHopperFromUUID(uuid);

                if (chunkHopper != null) {
                    Inventory inventory = Bukkit.createInventory(null, 54, name);
                    ItemStack[] stack = new ItemStack[hoppers.get(line).length];
                    for (int i = 0; i < stack.length; i++) {
                        if (hoppers.get(line)[i] == null) stack[i] = null;
                        else stack[i] = hoppers.get(line)[i].toItemStack();
                    }
                    inventory.setContents(stack);

                    inventoryList = chunkHopper.getInventories();

                    while (inventoryList.size() < num + 1)
                    {
                        inventoryList.add(Bukkit.createInventory(null, 54, Main.name));
                    }

                    inventoryList.set(num, inventory);

                    chunkHopper.setInventories(inventoryList);

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
        getServer().getPluginManager().registerEvents(new IslandDelete(dataHandler), this);
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
                for (Inventory inv : chunkhopper.getInventories()) {
                    String id = new StringBuilder(chunkhopper.getID() + ":" + chunkhopper.getInventories().indexOf(inv)).toString();
                    SerializableItemStack[] stacks;
                    if (inv != null) {
                        stacks = new SerializableItemStack[inv.getContents().length];
                        for (int i = 0; i < inv.getContents().length; i++) {
                            if (inv.getContents()[i] == null) {
                                stacks[i] = null;
                            } else {
                                stacks[i] = new SerializableItemStack(inv.getContents()[i]);
                            }
                        }
                    } else {
                        stacks = new SerializableItemStack[inv.getContents().length];
                    }
                    hoppers.put(id, stacks);
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