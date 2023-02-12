package fr.robotv2.robotdailyquests.listeners.block;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * @author Ordwen - https://github.com/Ordwen
 */

public class HarvestBlockListener extends QuestProgressionEnhancer {

    public HarvestBlockListener(RobotDailyQuest instance) {
        super(instance);
    }

    private interface CropFilter {
        Material filter(Material material);
    }

    private void handleCrops(Player player, BlockData data, Collection<ItemStack> stacks, CropFilter filter) {

        if(!(data instanceof Ageable ageable) || ageable.getAge() != ageable.getMaximumAge()) {
            return;
        }

        final Material material = filter.filter(data.getMaterial());
        final int amount = stacks.stream()
                .filter(stack -> stack.getType() == material)
                .mapToInt(ItemStack::getAmount)
                .sum();

        this.instance.debug("BlockBreakEvent - %s %d", material.name(), amount);
        this.increaseProgression(player, QuestType.FARMING, material, amount);
    }

    @EventHandler
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event) {

        final CropFilter filter = material -> switch (material) {
            case SWEET_BERRY_BUSH -> material = Material.SWEET_BERRIES;
            default -> material;
        };

        this.handleCrops(
                event.getPlayer(),
                event.getHarvestedBlock().getBlockData(),
                event.getItemsHarvested(),
                filter
        );
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        final CropFilter filter = material -> switch (material) {
            case POTATOES -> material = Material.POTATO;
            case CARROTS -> material = Material.CARROT;
            case BEETROOTS -> material = Material.BEETROOT;
            case COCOA -> material = Material.COCOA_BEANS;
            case SWEET_BERRY_BUSH -> material = Material.SWEET_BERRIES;
            default -> material;
        };

        this.handleCrops(
                event.getPlayer(),
                event.getBlock().getBlockData(),
                event.getBlock().getDrops(),
                filter
        );
    }
}
