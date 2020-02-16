package net.retrohopper.src.listener;

import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

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
        if ((event.getBlock().getType().equals(Material.HOPPER)) && (MiscUtils.getInstance().isUsedLocation(location))) {
            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromLocation(location);
            ItemStack[] contents = retrohopper.getInventory().getContents();
            level = retrohopper.getLevel();
            dataHandler.getHoppers().remove(retrohopper);
            player.sendMessage(ChatUtils.chat("&3&l[!] &bYou removed a retrohopper!"));
            event.setDropItems(false);
            if (player.getInventory().firstEmpty() != -1) player.getInventory().addItem(net.retrohopper.src.commands.Retrohopper.getHopperStack(1, level));
            else
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), net.retrohopper.src.commands.Retrohopper.getHopperStack(1, level));
            for (ItemStack stack : contents) {
                if (stack != null) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
                }
            }
        }
    }
}
