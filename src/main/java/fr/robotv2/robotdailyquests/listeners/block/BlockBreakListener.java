package fr.robotv2.robotdailyquests.listeners.block;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener extends QuestProgressionEnhancer {

    public BlockBreakListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {

        final Block block = event.getBlock();

        if(this.getGlitchChecker().isMarked(block)) {
            return;
        }

        this.increaseProgression(event.getPlayer(), QuestType.BREAK, block.getType(), 1);
    }
}
