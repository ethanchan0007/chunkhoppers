package net.retrohopper.src.listener;

import net.retrohopper.src.Main;
import net.retrohopper.src.nbt.NBT;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

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
        int level = 1;
        if (net.retrohopper.src.commands.Retrohopper.isRetrohopper(event.getItemInHand()) && !event.isCancelled()) {
                if (!MiscUtils.getInstance().isInUsedChunk(block.getChunk())) {
                    NBT nbt = NBT.get(event.getItemInHand());
                    if (nbt.hasKey("Level")) {
                        level = nbt.getInt("Level");
                    }
                    player.sendMessage(ChatUtils.chat("&3&l[!] &bYou placed a retrohopper!"));
                    dataHandler.getHoppers().add(new Retrohopper(block.getLocation(), Bukkit.createInventory(null, 54, Main.name), MiscUtils.itemFilterList(), UUID.randomUUID().toString(), level));
                } else {
                    player.sendMessage(ChatUtils.chat("&4&l[!] &cOnly one retrohopper is allowed per chunk!"));
                    event.setCancelled(true);
                }

        }
    }

}
