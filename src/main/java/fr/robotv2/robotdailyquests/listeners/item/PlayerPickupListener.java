package fr.robotv2.robotdailyquests.listeners.item;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PlayerPickupListener extends QuestProgressionEnhancer {

    public PlayerPickupListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPick(EntityPickupItemEvent event) {

        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        final Item item = event.getItem();

        if(this.getGlitchChecker().isMarked(item)) {
            return;
        }

        this.increaseProgression(player, QuestType.PICKUP, item.getItemStack().getType(), item.getItemStack().getAmount());
    }
}
