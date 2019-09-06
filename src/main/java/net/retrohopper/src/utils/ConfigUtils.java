package net.retrohopper.src.utils;

import net.retrohopper.src.Main;
import net.retrohopper.src.commands.Retrohopper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ConfigUtils {

	public static ConfigUtils instance = new ConfigUtils();

	public static ConfigUtils getInstance() {
		return instance;
	}

	public static HashMap<Entry<UUID, String>, List<Location>> blockInfo = new HashMap<Entry<UUID, String>, List<Location>>();
	private static HashMap<Location, ItemStack> dropCheck = new HashMap<Location, ItemStack>();

	public File getFile(String filename) {
		File file = new File(Main.getPlugin().getDataFolder(), filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return file;

	}

	public List<Location> getAllCustomBlocksInFile(File file, World world, String type)
	{
		List<Location> locationList = new ArrayList<Location>();
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		List<String> coordlist = config.getStringList(world + "." + type);
		if (!(coordlist == null))
		{
			for (String location : coordlist)
			{
				String[] loclist = location.split(",");
				Location loc = new Location(world, Double.parseDouble(loclist[0]),
						Double.parseDouble(loclist[1]), Double.parseDouble(loclist[2]));
				locationList.add(loc);
			}
			return locationList;
		}
		return null;

	}

	public void writeBlockLocToConfig(File file, Block block, String type) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String world = block.getWorld().getName();
		double x = block.getX();
		double y = block.getY();
		double z = block.getZ();
		String coord = x + "," + y + "," + z;
		List<String> coordlist = config.getStringList(world + "." + type);
		Location loc = block.getLocation();
		if (coordlist.contains(coord))
			return;
		Entry<UUID, String> entry = new AbstractMap.SimpleEntry<UUID, String>(block.getWorld().getUID(),
				block.getLocation().getChunk().toString());
		if (!ConfigUtils.blockInfo.containsKey(entry)) {
			List<Location> list = new ArrayList<Location>();
			list.add(loc);

			blockInfo.put(entry, list);
		} else {
			blockInfo.get(entry).add(loc);
		}
		coordlist.add(coord);
		config.set(world + "." + type, coordlist);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean isBlockLocInConfig(File file, Block block, String type) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String world = block.getWorld().getName();
		double x = block.getX();
		double y = block.getY();
		double z = block.getZ();
		String coord = x + "," + y + "," + z;
		List<String> coordlist = config.getStringList(world + "." + type);
		if (!(coordlist == null) && coordlist.contains(coord)) {
			return true;
		}
		return false;

	}

	public void removeBlockLocInConfig(File file, Block block, String type, Player player)
    {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String world = block.getWorld().getName();
		double x = block.getX();
		double y = block.getY();
		double z = block.getZ();
		String coord = x + "," + y + "," + z;
		List<String> coordlist = config.getStringList(world + "." + type);
		if (isBlockLocInConfig(file, block, type))
		{
			block.setType(Material.AIR);
			block.breakNaturally(new ItemStack(Material.AIR));
			coordlist.remove(coord);
			config.set(world + "." + type, coordlist);
			try
            {
				config.save(file);
			} catch (IOException e)
            {
				e.printStackTrace();
			}
			if (type.equals("chunkhopper"))
			{
				player.sendMessage(ChatUtils.chat("&c&l[!] &cDestroyed a chunk hopper!"));
				if (player.getInventory().firstEmpty() == -1)
					player.getLocation().getWorld().dropItemNaturally(player.getLocation(), Retrohopper.getHopperStack(1));
				else
					player.getInventory().addItem(Retrohopper.getHopperStack(1));
				return;
			}
		}

	}

	public boolean customBlockAlreadyInChunk(File file, Block block, String type)
    {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String world = block.getWorld().getName();
		double x = block.getX();
		double y = block.getY();
		double z = block.getZ();
		String coord = x + "," + y + "," + z;
		List<String> coordlist = config.getStringList(world + "." + type);
		for (String location : coordlist)
		{
			String[] loclist = location.split(",");
			Location loc = new Location(block.getWorld(), Double.parseDouble(loclist[0]),
					Double.parseDouble(loclist[1]), Double.parseDouble(loclist[2]));
			if (loc.getChunk().equals(block.getChunk()))
			{
				return true;
			}
		}
		return false;
	}

	public Location getCustomBlockInChunk(File file, Block block, String type)
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String world = block.getWorld().getName();
		double x = block.getX();
		double y = block.getY();
		double z = block.getZ();
		String coord = x + "," + y + "," + z;
		List<String> coordlist = config.getStringList(world + "." + type);
		for (String location : coordlist)
		{
			String[] loclist = location.split(",");
			Location loc = new Location(block.getWorld(), Double.parseDouble(loclist[0]),
					Double.parseDouble(loclist[1]), Double.parseDouble(loclist[2]));
			if (loc.getChunk().equals(block.getChunk()))
			{
				return loc;
			}
		}
		return null;
	}

    public static String InventoryToString(Inventory invInventory)
    {
        String serialization = invInventory.getSize() + ";";
        for (int i = 0; i < invInventory.getSize(); i++)
        {
            ItemStack is = invInventory.getItem(i);
            if (is != null)
            {
                String serializedItemStack = new String();

                String isType = String.valueOf(is.getType().getId());
                serializedItemStack += "t@" + isType;

                if (is.getDurability() != 0)
                {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack += ":d@" + isDurability;
                }

                if (is.getAmount() != 1)
                {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack += ":a@" + isAmount;
                }

                Map<Enchantment,Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0)
                {
                    for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
                    {
                        serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
                    }
                }

                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
    }

    public static Inventory StringToInventory(String invString)
    {
        String[] serializedBlocks = invString.split(";");
        String invInfo = serializedBlocks[0];
        Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));

        for (int i = 1; i < serializedBlocks.length; i++)
        {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);

            if (stackPosition >= deserializedInventory.getSize())
            {
                continue;
            }

            ItemStack is = null;
            Boolean createdItemStack = false;

            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack)
            {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t"))
                {
                    is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    createdItemStack = true;
                }
                else if (itemAttribute[0].equals("d") && createdItemStack)
                {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("a") && createdItemStack)
                {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("e") && createdItemStack)
                {
                    is.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                }
            }
            deserializedInventory.setItem(stackPosition, is);
        }

        return deserializedInventory;
    }

}
