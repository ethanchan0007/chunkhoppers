package net.retrohopper.src.listener;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import net.retrohopper.src.Main;
import net.retrohopper.src.objects.Retrohopper;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import net.retrohopper.src.utils.WorldUtils;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemSpawnEvent implements Listener {

    private DataHandler dataHandler;

    public ItemSpawnEvent(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @EventHandler
    public void itemCreate(org.bukkit.event.entity.ItemSpawnEvent event) {
        Location itemLoc = event.getEntity().getLocation();
        Chunk chunk = itemLoc.getChunk();

        if (WorldUtils.isWorldLoaded(itemLoc.getWorld()) && chunk != null) {
            Retrohopper retrohopper = MiscUtils.getInstance().getHopperFromChunk(chunk);
            Inventory inventory = retrohopper.getInventory();
            ItemStack item = WildStackerAPI.getStackedItem(event.getEntity()).getItemStack();
            if (inventory != null && !MiscUtils.isInventoryFull(inventory) && !event.isCancelled()) {
                for (ItemStack i : retrohopper.getItemFilterList().keySet())
                {
                    Main.logger.info(Boolean.toString(i.getData().equals(item.getData())));
                    if (i.getType() == item.getType() && retrohopper.getItemFilterList().get(i))
                    {
                        inventory.addItem(event.getEntity().getItemStack());
                        event.getEntity().getLocation().getWorld().playEffect(itemLoc, Effect.SMOKE, 1);
                        event.getEntity().remove();
                        retrohopper.setInventory(inventory);
                    }
                }
            }
        }
    }
}
