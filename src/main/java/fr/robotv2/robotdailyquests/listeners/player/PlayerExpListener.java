package fr.robotv2.robotdailyquests.listeners.player;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PlayerExpListener extends QuestProgressionEnhancer {

    public PlayerExpListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExpChange(PlayerExpChangeEvent event) {
        this.increaseProgression(event.getPlayer(), QuestType.EXP_POINTS, (Object) null, event.getAmount());
    }
}
