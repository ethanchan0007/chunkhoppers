package net.retrohopper.src.listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import javafx.scene.chart.PieChart;
import net.retrohopper.src.Main;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;
import java.util.UUID;

public class HopperPlace implements Listener {

    private DataHandler dataHandler;

    public HopperPlace(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (event.getItemInHand().equals(net.retrohopper.src.commands.Retrohopper.getHopperStack(event.getItemInHand().getAmount()))) {
            if (event.getBlock().getWorld() == SuperiorSkyblockAPI.getIslandsWorld()) {
                if (!MiscUtils.getInstance().isInUsedChunk(block.getChunk())) {
                    player.sendMessage(ChatUtils.chat("&3&l[!] &bYou placed a retrohopper!"));
                    dataHandler.getHoppers().add(new Retrohopper(block.getLocation(), Bukkit.createInventory(null, 54, Main.name), MiscUtils.itemFilterList(), UUID.randomUUID().toString(), 1));
                } else {
                    player.sendMessage(ChatUtils.chat("&4&l[!] &cOnly one retrohopper is allowed per chunk!"));
                    event.setCancelled(true);
                }
            } else
            {
                player.sendMessage(ChatUtils.chat("&4&l[!] &cYou can only place retrohoppers in the island world!"));
                event.setCancelled(true);
            }
        }
    }

}
