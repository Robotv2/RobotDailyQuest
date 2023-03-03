package fr.robotv2.robotdailyquests.listeners.entity;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class EntityShearListener extends QuestProgressionEnhancer {

    public EntityShearListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        this.increaseProgression(event.getPlayer(), QuestType.SHEAR, event.getEntity().getType(), 1);
    }
}
