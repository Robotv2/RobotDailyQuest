package fr.robotv2.robotdailyquests.listeners.block;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
import fr.robotv2.robotdailyquests.events.MultipleCropsBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HarvestBlockListener extends QuestProgressionEnhancer {

    public HarvestBlockListener(RobotDailyQuest instance) {
        super(instance);
    }

    private interface CropFilter {
        Material filter(Material material);
    }

    private void checkAboveBlock(Player player, Block initial) {
        if(initial.getType() == Material.BAMBOO || initial.getType() == Material.SUGAR_CANE) {

            final List<Block> blocks = new ArrayList<>();
            Block above = initial.getRelative(BlockFace.UP);

            while(above.getType() == Material.BAMBOO || above.getType()== Material.SUGAR_CANE) {
                blocks.add(above);
                above = above.getRelative(BlockFace.UP);
            }

            final MultipleCropsBreakEvent multipleCropsBreakEvent = new MultipleCropsBreakEvent(player, initial.getType(), blocks);
            Bukkit.getPluginManager().callEvent(multipleCropsBreakEvent);
        }
    }

    private void handleCrops(Player player, BlockData data, Collection<ItemStack> stacks, @Nullable CropFilter filter) {

        if(!(data instanceof Ageable ageable) || ageable.getAge() != ageable.getMaximumAge()) {
            return;
        }

        final Material material = filter != null ? filter.filter(data.getMaterial()) : data.getMaterial();

        final int amount = stacks.stream()
                .filter(stack -> stack.getType() == material)
                .mapToInt(ItemStack::getAmount)
                .sum();

        this.increaseProgression(player, QuestType.FARMING, material, amount);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMultipleCropsBreak(MultipleCropsBreakEvent event) {

        final int amount = event.getBlocks().stream()
                .flatMap(block -> block.getDrops().stream())
                .filter(stack -> stack.getType() == event.getMaterial())
                .mapToInt(ItemStack::getAmount)
                .sum();

        this.increaseProgression(event.getPlayer(), QuestType.FARMING, event.getMaterial(), amount);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
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

        this.checkAboveBlock(event.getPlayer(), event.getBlock());

        this.handleCrops(
                event.getPlayer(),
                event.getBlock().getBlockData(),
                event.getBlock().getDrops(),
                filter
        );
    }
}
