package fr.robotv2.robotdailyquests.listeners.entity;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;

public class EntityBreedListener extends QuestProgressionEnhancer {

    public EntityBreedListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityBread(EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player player) {
            this.increaseProgression(player, QuestType.BREED, event.getEntityType(), 1);
        }
    }
}
