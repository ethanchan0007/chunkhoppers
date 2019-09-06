package net.retrohopper.src;

import net.retrohopper.src.commands.Retrohopper;
import net.retrohopper.src.listener.AllListener;
import net.retrohopper.src.serializable.SerializableItemStack;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Hopper;
import org.bukkit.plugin.java.JavaPlugin;
import com.wasteofplastic.askyblock.ASkyBlockAPI;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class Main extends JavaPlugin
{
    public static Main plugin;
    static Logger logger;
    private TimerTask task;
    public static final String name = ChatUtils.chat("&3Retro&bhopper");
    public HashMap<String, SerializableItemStack[]> hoppers;
    private final File customblocks = ConfigUtils.getInstance().getFile("retrohoppers.yml");

    public void onEnable()
    {
        plugin = this;
        logger = getLogger();

        loadConfigFiles();
        loadFiles();
        loadEvents();
        loadCommands();
        hopperTimer();

        getLogger().info("Chunk Hopper enabled");
    }

    public void hopperTimer()
    {
        this.task = new TimerTask()
        {
            public void run()
            {
                for (Location l : HopperManager.getHoppers().keySet())
                {
                    // getLogger().info(l.getChunk().toString()); debug
                    if (l.getChunk() != null)
                    {
                        Hopper hopper = (Hopper) l.getBlock().getState().getData();
                        if ((l.getBlock().getRelative(hopper.getFacing()).getState() instanceof InventoryHolder))
                        {
                            InventoryHolder ih = (InventoryHolder) l.getBlock().getRelative(hopper.getFacing()).getState();
                            if (ih != null)
                            {
                                ArrayList<Integer> nonNull = new ArrayList();
                                Inventory inv = (Inventory) HopperManager.getHoppers().get(l);
                                for (int i = 0; i < inv.getContents().length; i++)
                                {
                                    if (inv.getContents()[i] != null) nonNull.add(Integer.valueOf(i));

                                }
                                if (nonNull.size() >= 2)
                                {
                                    ItemStack[] contents = inv.getContents();
                                    ih.getInventory().addItem(new ItemStack[]{contents[((Integer) nonNull.get(0)).intValue()], contents[((Integer) nonNull.get(1)).intValue()]});
                                    contents[((Integer) nonNull.get(0)).intValue()] = null;
                                    contents[((Integer) nonNull.get(1)).intValue()] = null;
                                    inv.setContents(contents);
                                } else if (nonNull.size() == 1)
                                {
                                    ItemStack[] contents = inv.getContents();
                                    ih.getInventory().addItem(new ItemStack[]{contents[((Integer) nonNull.get(0)).intValue()]});
                                    contents[((Integer) nonNull.get(0)).intValue()] = null;
                                    inv.setContents(contents);
                                }
                            }
                        }
                    }

                }
            }
        };
        new Timer().scheduleAtFixedRate(this.task, 30000L, 5000L);
    }

    public void loadFiles()
    {
        List<Location> locList = ConfigUtils.getInstance().getAllCustomBlocksInFile(customblocks, ASkyBlockAPI.getInstance().getIslandWorld(),"chunkhopper");
        if (locList != null)
        {

            getLogger().info("Got " + locList.size() + " hoppers!");
        }
        if (new File("plugins/chunkHoppers.ser").exists())
        {
            ObjectInputStream ois;
            try
            {
                FileInputStream fis = new FileInputStream("plugins/chunkHoppers.ser");

                ois = new ObjectInputStream(fis);
                hoppers = (HashMap)ois.readObject();
                ois.close();
                fis.close();
            } catch (IOException ioe)
            {
                ioe.printStackTrace();
                return;
            } catch (ClassNotFoundException c)
            {
                System.out.println("Class not found");
                c.printStackTrace(); return;
            }
            HashMap<Location, Inventory> finalized = new HashMap();
            for (String s : hoppers.keySet())
            {
                String[] loc = s.split(":");
                Inventory inventory = Bukkit.createInventory(null, 54, name);
                ItemStack[] stack = new ItemStack[((SerializableItemStack[])hoppers.get(s)).length];
                for (int i = 0; i < stack.length; i++)
                {
                    if (((SerializableItemStack[])hoppers.get(s))[i] == null) stack[i] = null;
                    else stack[i] = ((SerializableItemStack[])hoppers.get(s))[i].toItemStack();
                }
                inventory.setContents(stack);
                finalized.put(new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3])), inventory);
            }
            getLogger().info("Got " + finalized.size() + " hoppers!");
            HopperManager.setHoppers(finalized);
            getLogger().info("Successfully loaded (deserialized) chunkHoppers.ser");
        }
        else
        {
            getLogger().info("Couldn't find chunkHoppers.ser, using blank.");
        }
    }

    public void loadCommands()
    {
        getCommand("retrohopper").setExecutor(new Retrohopper());
    }

    public void loadEvents()
    {
        getServer().getPluginManager().registerEvents(new AllListener(), this);
    }

    public void onDisable()
    {
        this.task.cancel();
        try
        {
            HashMap<String, SerializableItemStack[]> hoppers = new HashMap();
            for (Location loc : HopperManager.getHoppers().keySet())
            {
                SerializableItemStack[] stacks = new SerializableItemStack[((Inventory)HopperManager.getHoppers().get(loc)).getContents().length];
                for (int i = 0; i < ((Inventory)HopperManager.getHoppers().get(loc)).getContents().length; i++) {
                    if (((Inventory)HopperManager.getHoppers().get(loc)).getContents()[i] == null) {
                        stacks[i] = null;
                    } else {
                        stacks[i] = new SerializableItemStack(((Inventory)HopperManager.getHoppers().get(loc)).getContents()[i]);
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
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        getLogger().info("Chunk Hopper disabled");
    }

    public void loadConfigFiles()
    {
        File retrohoppersFile = new File(this.getDataFolder().getAbsolutePath(), "retrohoppers.yml");

        if (!retrohoppersFile.exists())
        {
            try {
                retrohoppersFile.createNewFile();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static Main getPlugin()
    {
        return plugin;
    }
}