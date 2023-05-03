package fr.robotv2.robotdailyquests.listeners.item;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class PlayerEnchantItemListener extends QuestProgressionEnhancer {

    public PlayerEnchantItemListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        this.increaseProgression(event.getEnchanter(), QuestType.ENCHANT, event.getItem().getType(), 1);
    }
}
