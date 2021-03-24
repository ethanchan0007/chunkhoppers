package net.chunkhopper.src.commands;

import net.chunkhopper.src.Main;
import net.chunkhopper.src.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Chunk
        implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player = (Player) sender;
        if (!(sender instanceof Player)) return false;
        MiscUtils.highlightChunk(player.getLocation(), player);
        return true;
    }
}
