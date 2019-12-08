package net.retrohopper.src.listener;

import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import net.retrohopper.src.utils.DataHandler;
import net.retrohopper.src.utils.MiscUtils;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class IslandDelete implements Listener {

    private DataHandler dataHandler;

    public IslandDelete(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    @EventHandler
    public void onIslandDelete(IslandDisbandEvent event) {
        ArrayList<Chunk> arrayList = new ArrayList();
        for (Chunk chunk : event.getIsland().getAllChunks())
        {
            if (!arrayList.contains(chunk)) {
                arrayList.add(chunk);
            }
        }
        for (Chunk chunk : arrayList) {
            if (MiscUtils.getInstance().isInUsedChunk(chunk) && MiscUtils.getInstance().getHopperLocationFromChunk(chunk) != null) {
                dataHandler.getHoppers().remove(MiscUtils.getInstance().getHopperFromChunk(chunk));
            }
        }
    }
}
