package fr.robotv2.robotdailyquests.listeners.item;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerConsumeListener extends QuestProgressionEnhancer {

    public PlayerConsumeListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        this.increaseProgression(event.getPlayer(), QuestType.CONSUME, event.getItem().getType().name());
    }
}
