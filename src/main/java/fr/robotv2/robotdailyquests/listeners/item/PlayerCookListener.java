package fr.robotv2.robotdailyquests.listeners.item;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class PlayerCookListener extends QuestProgressionEnhancer {

    public PlayerCookListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        this.increaseProgression(event.getPlayer(), QuestType.COOK, event.getItemType(), event.getItemAmount());
    }
}
