package fr.robotv2.robotdailyquests.listeners.block;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Ordwen - https://github.com/Ordwen
 */

public class HarvestBlockListener extends QuestProgressionEnhancer {

    public HarvestBlockListener(RobotDailyQuest instance) {
        super(instance);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event) {

        final BlockData data = event.getHarvestedBlock().getBlockData();

        if (!(data instanceof Ageable ageable) || ageable.getAge() != ageable.getMaximumAge()) {
            return;
        }

        Material material = data.getMaterial();
        if(material == Material.SWEET_BERRY_BUSH) {
            material = Material.SWEET_BERRIES;
        }

        final List<ItemStack> drops = event.getItemsHarvested();
        int amount = 0;

        for (ItemStack item : drops) {
            if (item.getType() == material) {
                amount += item.getAmount();
            }
        }

        this.increaseProgression(event.getPlayer(), QuestType.FARMING, material, amount);
    }
}
