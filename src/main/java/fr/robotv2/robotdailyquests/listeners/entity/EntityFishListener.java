package fr.robotv2.robotdailyquests.listeners.entity;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

public class EntityFishListener extends QuestProgressionEnhancer {

    public EntityFishListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {

        final Entity entity = event.getCaught();

        if(entity == null) {
            return;
        }

        if(entity instanceof Item item) {
            this.increaseProgression(event.getPlayer(), QuestType.FISH, item.getType(), 1);
        } else {
            this.increaseProgression(event.getPlayer(), QuestType.FISH, entity.getType(), 1);
        }
    }
}
