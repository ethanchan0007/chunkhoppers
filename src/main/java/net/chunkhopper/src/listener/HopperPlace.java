package net.chunkhopper.src.listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.chunkhopper.src.commands.ChunkHopperCmd;
import net.chunkhopper.src.nbt.NBT;
import net.chunkhopper.src.objects.ChunkHopper;
import net.chunkhopper.src.utils.DataHandler;
import net.chunkhopper.src.Main;
import net.chunkhopper.src.utils.ChatUtils;
import net.chunkhopper.src.utils.ItemBuilder;
import net.chunkhopper.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
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
        int invSize = 54;
        if (ItemBuilder.isRetrohopper(event.getItemInHand()) && !event.isCancelled() && SuperiorSkyblockAPI.getIslandsWorld().equals(player.getWorld())) {
                if (!MiscUtils.getInstance().isInUsedChunk(block.getChunk())) {
                    NBT nbt = NBT.get(event.getItemInHand());
                    if (nbt.hasKey("Level")) {
                        level = nbt.getInt("Level");
                    }
                    if (nbt.hasKey("invSize")) {
                        invSize = nbt.getInt("invSize");
                    }
                    player.sendMessage(ChatUtils.chat("&3&l[!] &bYou placed a retrohopper!"));
                    dataHandler.getHoppers().add(new ChunkHopper(block.getLocation(), (LinkedHashMap<ItemStack, Boolean>) MiscUtils.itemFilterList().clone(), UUID.randomUUID().toString(), level, invSize));
                } else {
                    player.sendMessage(ChatUtils.chat("&4&l[!] &cOnly one retrohopper is allowed per chunk!"));
                    event.setCancelled(true);
                }

        }
    }

}
