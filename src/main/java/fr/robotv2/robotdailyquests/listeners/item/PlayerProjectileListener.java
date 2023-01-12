package fr.robotv2.robotdailyquests.listeners.item;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class PlayerProjectileListener extends QuestProgressionEnhancer {

    public PlayerProjectileListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onProjectile(ProjectileLaunchEvent event) {

        if(!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        this.increaseProgression(player, QuestType.LAUNCH, event.getEntity().getType(), 1);
    }
}
