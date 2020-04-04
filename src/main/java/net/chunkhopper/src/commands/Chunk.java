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
        org.bukkit.Chunk chunk = player.getLocation().getChunk();
        Location corner1 = chunk.getBlock(0, 0, 0).getLocation();
        Location corner2 = chunk.getBlock(0, 0, 0).getLocation().add(16, 255, 16);
        List<Location> particleLocList = MiscUtils.getInstance().getHollowCube(corner1, corner2);
        for (final Location particleLoc : particleLocList) {
            new BukkitRunnable() {
                int i = 0;

                public void run() {
                    player.getWorld().spawnParticle(Particle.BARRIER, particleLoc, 0, 1, 0.001, 0, 1);
                    if (i == 20) cancel();
                    i++;
                }
            }.runTaskTimer(Main.getPlugin(), 0, 5);
        }
        return true;
    }
}
