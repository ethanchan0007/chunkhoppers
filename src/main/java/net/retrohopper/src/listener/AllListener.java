package net.retrohopper.src.listener;

import net.retrohopper.src.HopperManager;
import net.retrohopper.src.commands.Retrohopper;
import net.retrohopper.src.utils.ChatUtils;
import net.retrohopper.src.utils.ConfigUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import sun.security.krb5.Config;

import java.io.File;

public class AllListener
        implements Listener
{
    private final File customblocks = ConfigUtils.getInstance().getFile("retrohoppers.yml");
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (event.getItemInHand().equals(Retrohopper.getHopperStack(event.getItemInHand().getAmount())))
        {
            //if (!HopperManager.hasChunk(event.getBlockPlaced().getChunk()))
            if (!ConfigUtils.getInstance().customBlockAlreadyInChunk(customblocks, block, "chunkhopper"))
            {
                player.sendMessage(ChatUtils.chat("&3&l[!] &bYou placed a retrohopper!"));
                HopperManager.registerHopper(event.getBlockPlaced().getLocation());
                ConfigUtils.getInstance().writeBlockLocToConfig(customblocks, block, "chunkhopper");
            }
            else
            {
                player.sendMessage(ChatUtils.chat("&4&l[!] &cOnly one retrohopper is allowed per chunk!"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if ((event.getBlock().getType().equals(Material.HOPPER)) && ConfigUtils.getInstance().isBlockLocInConfig(customblocks, event.getBlock(), "chunkhopper"))
        //if ((event.getBlock().getType().equals(Material.HOPPER)) && (HopperManager.hasExactLocation(event.getBlock().getLocation()))) {
        {
            ItemStack[] contents = HopperManager.getInventory(event.getBlock().getLocation().getChunk()).getContents();
            HopperManager.deleteHopper(event.getBlock().getLocation());
            ConfigUtils.getInstance().removeBlockLocInConfig(customblocks, event.getBlock(), "chunkhopper", player);
            player.sendMessage(ChatUtils.chat("&3&l[!] &bYou removed a retrohopper!"));
            event.setDropItems(false);
            if (player.getInventory().firstEmpty() != -1) player.getInventory().addItem(Retrohopper.getHopperStack(1));
            else
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Retrohopper.getHopperStack(1));
            for (ItemStack stack : contents) {
                if (stack != null) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event)
    {
        if (HopperManager.hasExactLocation(event.getInventory().getLocation()))
        {
            event.setCancelled(true);
            event.getPlayer().openInventory(HopperManager.getInventory(event.getInventory().getLocation().getChunk()));
        }
    }

    @EventHandler
    public void itemCreate(ItemSpawnEvent event) {
        Chunk chunk = event.getEntity().getLocation().getChunk();
        try {
            Location location = ConfigUtils.getInstance().getCustomBlockInChunk(customblocks, event.getEntity().getLocation().getBlock(), "chunkhopper");
            Inventory inventory = HopperManager.getInventory(chunk);
            if ((inventory != null) && (inventory.firstEmpty() != -1) && (!event.isCancelled()) && (!event.getEntity().isDead())) {
                inventory.addItem(new ItemStack[]{((Item) event.getEntity()).getItemStack()});
                event.getEntity().remove();
                HopperManager.setInventory(location, inventory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
