package fr.robotv2.robotdailyquests.listeners;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

public class GlitchChecker implements Listener {

    public static final String METADATA_KEY = "robot_quest_marked";

    private final RobotDailyQuest instance;
    private final MetadataValue value;

    public GlitchChecker(RobotDailyQuest instance) {
        this.instance = instance;
        this.value = new FixedMetadataValue(instance, METADATA_KEY);
    }

    public void mark(Metadatable source) {
        source.setMetadata(METADATA_KEY, value);
    }

    public void unMark(Metadatable source) {
        source.removeMetadata(METADATA_KEY, instance);
    }

    public boolean isMarked(Metadatable source) {
        return source.hasMetadata(METADATA_KEY);
    }

    /* Listeners */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {

        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return; // Do not mark if the player is in creative.
        }

        this.mark(event.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        this.mark(event.getItemDrop());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(SpawnerSpawnEvent event) {
        this.mark(event.getEntity());
    }
}
