package net.chunkhopper.src.listener;

import net.chunkhopper.src.commands.ChunkHopperCmd;
import net.chunkhopper.src.objects.ChunkHopper;
import net.chunkhopper.src.utils.DataHandler;
import net.chunkhopper.src.utils.UMaterial;
import net.chunkhopper.src.utils.ChatUtils;
import net.chunkhopper.src.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class BlockBreak implements Listener {

    private DataHandler dataHandler;

    public BlockBreak(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        int level = 1;
        if (event.getBlock().getType() == Material.NETHER_BRICK) {
            event.setDropItems(false);
            location.getWorld().dropItemNaturally(location, new ItemStack(UMaterial.NETHER_BRICK.getMaterial(), 4));
            return;
        }
        ArrayList<ItemStack> contents = new ArrayList<ItemStack>();
        if ((event.getBlock().getType().equals(Material.HOPPER)) && (MiscUtils.getInstance().isUsedLocation(location))) {
            ChunkHopper chunkhopper = MiscUtils.getInstance().getHopperFromLocation(location);
            level = chunkhopper.getLevel();
            dataHandler.getHoppers().remove(chunkhopper);
            player.sendMessage(ChatUtils.chat("&3&l[!] &bYou removed a chunkhopper!"));
            event.setDropItems(false);
            if (player.getInventory().firstEmpty() != -1)
                player.getInventory().addItem(ChunkHopperCmd.getHopperStack(1, level));
            else
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), ChunkHopperCmd.getHopperStack(1, level));
            try {
                for (Inventory i : chunkhopper.getInventoryList()) {
                    for (ItemStack j : i.getContents()) {
                        if (j != null) contents.add(j);
                    }
                    for (ItemStack stack : contents) {
                        if (stack != null) {
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
                        }
                    }
                }
            } catch (NullPointerException f) {
                return;
            }
        }
    }
}
