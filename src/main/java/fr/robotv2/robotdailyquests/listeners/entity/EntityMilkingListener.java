package fr.robotv2.robotdailyquests.listeners.entity;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

public class EntityMilkingListener extends QuestProgressionEnhancer {

    public EntityMilkingListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMilk(PlayerBucketFillEvent event) {

        final ItemStack bucket = event.getItemStack();

        if(bucket == null) {
            return;
        }

        if (bucket.getType() == Material.MILK_BUCKET) {
            this.increaseProgression(event.getPlayer(), QuestType.MILKING, EntityType.COW.name());
        }
    }
}
