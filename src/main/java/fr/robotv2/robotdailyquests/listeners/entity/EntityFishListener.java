package fr.robotv2.robotdailyquests.listeners.entity;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

public class EntityFishListener extends QuestProgressionEnhancer {

    public EntityFishListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {

        if(event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        final Player player = event.getPlayer();
        final Entity entity = event.getCaught();

        if(entity == null) {
            return;
        }

        this.instance.debug("%s has caught an entity by fishing.", player.getName());

        if(entity instanceof Item item) {
            this.increaseProgression(player, QuestType.FISH, item.getItemStack().getType(), 1);
        } else {
            this.increaseProgression(player, QuestType.FISH, entity.getType(), 1);
        }
    }
}
