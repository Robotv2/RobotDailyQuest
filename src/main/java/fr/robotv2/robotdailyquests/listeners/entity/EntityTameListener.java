package fr.robotv2.robotdailyquests.listeners.entity;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTameEvent;

public class EntityTameListener extends QuestProgressionEnhancer {

    public EntityTameListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onTame(EntityTameEvent event) {
        if (event.getOwner() instanceof Player player) {

            Cow cow;
            this.increaseProgression(player, QuestType.TAME, event.getEntityType(), 1);
        }
    }
}
