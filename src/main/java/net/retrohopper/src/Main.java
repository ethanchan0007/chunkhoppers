package net.retrohopper.src;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.retrohopper.src.listener.*;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.serializable.SerializableItemStack;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import net.retrohopper.src.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Hopper;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static Main plugin;
    public static Logger logger;
    private TimerTask task;
    public static final String name = ChatUtils.chat("&3Retro&bhopper");
    public HashMap<String, SerializableItemStack[]> hoppers;
    public DataHandler dataHandler = new DataHandler();

    public void onEnable() {
        plugin = this;
        logger = getLogger();
        if (!getServer().getWorlds().contains(SuperiorSkyblockAPI.getIslandsWorld()))
            getServer().getWorlds().add(SuperiorSkyblockAPI.getIslandsWorld());

        dataHandler.retrieveData();
        loadInventories();
        loadEvents();
        loadCommands();

        hopperTimer();

        getLogger().info("Chunk Hopper enabled");
    }

    public void hopperTimer() {
        new BukkitRunnable() {

            public void run() {
                if (!dataHandler.getHoppers().isEmpty()) {
                    for (Retrohopper retrohopper : dataHandler.getHoppers()) {
                        Location l = retrohopper.getLocation();
                        if (WorldUtils.isWorldLoaded(l.getWorld()) && l.getChunk() != null) {
                            Hopper hopper = (Hopper) l.getBlock().getState().getData();
                            if ((l.getBlock().getRelative(hopper.getFacing()).getState() instanceof InventoryHolder)) {
                                InventoryHolder ih = (InventoryHolder) l.getBlock().getRelative(hopper.getFacing()).getState();
                                if (ih != null) {
                                    if (!(ih.getInventory().firstEmpty() == -1)) {
                                        ArrayList<Integer> nonNull = new ArrayList();
                                        Inventory inv = retrohopper.getInventory();
                                        for (int i = 0; i < inv.getContents().length; i++) {
                                            if (inv.getContents()[i] != null) nonNull.add(Integer.valueOf(i));

                                        }
                                        if (nonNull.size() >= 2) {
                                            ItemStack[] contents = inv.getContents();
                                            for (int i = 0; i < 2*retrohopper.getMultiplier(); i ++)
                                            {
                                                ih.getInventory().addItem(contents[nonNull.get(i).intValue()]);
                                                contents[nonNull.get(i).intValue()] = null;
                                            }
                                            inv.setContents(contents);
                                        } else if (nonNull.size() == 1) {
                                            ItemStack[] contents = inv.getContents();
                                            ih.getInventory().addItem(contents[nonNull.get(0).intValue()]);
                                            contents[nonNull.get(0).intValue()] = null;
                                            inv.setContents(contents);
                                        }
                                    }
                                }
                            }
                        }
                        dataHandler.saveData();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 100L);
    }

    public void loadInventories() {
        int finalizedCount = 0;
        if (new File("plugins/chunkHoppers.ser").exists()) {
            ObjectInputStream ois;
            try {
                FileInputStream fis = new FileInputStream("plugins/chunkHoppers.ser");

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
                String[] loc = s.split(":");
                Inventory inventory = Bukkit.createInventory(null, 54, name);
                ItemStack[] stack = new ItemStack[hoppers.get(s).length];
                for (int i = 0; i < stack.length; i++) {
                    if (hoppers.get(s)[i] == null) stack[i] = null;
                    else stack[i] = hoppers.get(s)[i].toItemStack();
                }
                inventory.setContents(stack);
                Location location = new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
                logger.info(location.toString());
                if (MiscUtils.getInstance().isUsedLocation(location))
                {
                    Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(location);
                    retrohopper.setInventory(inventory);
                    finalizedCount++;
                }
            }
            getLogger().info("Got " + finalizedCount + " hoppers!");
            //Retrohopper.setHoppers(finalized);
            getLogger().info("Successfully loaded (deserialized) chunkHoppers.ser");
        }
    }

    public void loadCommands() {
        getCommand("retrohopper").setExecutor(new net.retrohopper.src.commands.Retrohopper());
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
        getLogger().info("Chunk Hopper disabled");
    }

    public void saveHopperContents()
    {
        try {
            HashMap<String, SerializableItemStack[]> hoppers = new HashMap();
            for (Retrohopper retrohopper : dataHandler.getHoppers()) {
                Location loc = retrohopper.getLocation();
                SerializableItemStack[] stacks = new SerializableItemStack[retrohopper.getInventory().getContents().length];
                for (int i = 0; i < retrohopper.getInventory().getContents().length; i++) {
                    if (retrohopper.getInventory().getContents()[i] == null) {
                        stacks[i] = null;
                    } else {
                        stacks[i] = new SerializableItemStack(retrohopper.getInventory().getContents()[i]);
                    }
                }
                hoppers.put(loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ(), stacks);
            }
            FileOutputStream fos = new FileOutputStream("plugins/chunkHoppers.ser");

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