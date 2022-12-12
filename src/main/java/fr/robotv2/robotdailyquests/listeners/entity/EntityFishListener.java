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

        final String type = entity instanceof Item item
                ? item.getItemStack().getType().name()
                : entity.getType().name();
        this.increaseProgression(event.getPlayer(), QuestType.FISH, type);
    }
}
