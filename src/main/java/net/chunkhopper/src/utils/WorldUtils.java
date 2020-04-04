package net.chunkhopper.src.utils;

import net.chunkhopper.src.Main;
import org.bukkit.World;

public class WorldUtils {
    public static boolean isWorldLoaded(World world)
    {
        Main plugin = Main.getPlugin();
        for (World w : Main.getPlugin().getServer().getWorlds())
        {
            if (w.equals(world))
            {
                return true;
            }
        }
        return false;
    }
}
