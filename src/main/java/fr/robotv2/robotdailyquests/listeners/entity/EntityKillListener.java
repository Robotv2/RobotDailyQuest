package fr.robotv2.robotdailyquests.listeners.entity;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityKillListener extends QuestProgressionEnhancer {

    public EntityKillListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityKill(EntityDeathEvent event) {

        final LivingEntity entity = event.getEntity();
        final Player player = entity.getKiller();

        if(this.getGlitchChecker().isMarked(entity)) {
            return; // entity is from a spawner.
        }

        if(player == null) {
            return;
        }

        this.increaseProgression(player, QuestType.KILL, entity.getType(), 1);
    }
}
