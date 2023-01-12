package fr.robotv2.robotdailyquests.listeners.item;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class PlayerCraftListener extends QuestProgressionEnhancer {

    public PlayerCraftListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCraft(InventoryClickEvent event) {

        if(!(event.getInventory() instanceof CraftingInventory)) {
            return;
        }

        if(event.getSlot() != 0) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final ItemStack item = event.getCurrentItem();

        if(item == null) {
            return;
        }

        this.increaseProgression(player, QuestType.CRAFT, item.getType(), item.getAmount());
    }
}
