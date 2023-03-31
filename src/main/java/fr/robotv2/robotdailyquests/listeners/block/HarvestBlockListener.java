package fr.robotv2.robotdailyquests.listeners.block;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.events.MultipleCropsBreakEvent;
import fr.robotv2.robotdailyquests.listeners.QuestProgressionEnhancer;
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
import java.util.EnumSet;
import java.util.List;

public class HarvestBlockListener extends QuestProgressionEnhancer {

    private final EnumSet<Material> VERTICAL_PROPS = EnumSet.of(
            Material.BAMBOO,
            Material.SUGAR_CANE,
            Material.KELP_PLANT,
            Material.CACTUS
    );

    public HarvestBlockListener(RobotDailyQuest instance) {
        super(instance);
    }

    @FunctionalInterface
    private interface CropFilter {
        Material filter(Material material);
    }

    private void checkAboveBlock(Player player, Block initial, @Nullable CropFilter filter) {

        if(!VERTICAL_PROPS.contains(initial.getType())) {
            return;
        }

        final List<Block> blocks = new ArrayList<>();
        blocks.add(initial);

        Block above = initial.getRelative(BlockFace.UP);

        while(VERTICAL_PROPS.contains(above.getType())) {
            blocks.add(above);
            above = above.getRelative(BlockFace.UP);
        }

        final Material material = filter != null ? filter.filter(initial.getType()) : initial.getType();
        final MultipleCropsBreakEvent multipleCropsBreakEvent = new MultipleCropsBreakEvent(player, material, blocks);

        Bukkit.getPluginManager().callEvent(multipleCropsBreakEvent);
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMultipleCropsBreak(MultipleCropsBreakEvent event) {

        final int amount = event.getBlocks().stream()
                .filter(block -> !this.getGlitchChecker().isMarked(block)) // Check if the block isn't placed by the player.
                .flatMap(block -> block.getDrops().stream())
                .filter(stack -> stack.getType() == event.getMaterial())
                .mapToInt(ItemStack::getAmount)
                .sum();

        this.increaseProgression(event.getPlayer(), QuestType.FARMING, event.getMaterial(), amount);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event) {

        final CropFilter filter = material -> switch (material) {
            case SWEET_BERRY_BUSH -> Material.SWEET_BERRIES;
            default -> material;
        };

        this.handleCrops(
                event.getPlayer(),
                event.getHarvestedBlock().getBlockData(),
                event.getItemsHarvested(),
                filter
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        final CropFilter filter = material -> switch (material) {
            case POTATOES -> Material.POTATO;
            case CARROTS -> Material.CARROT;
            case BEETROOTS -> Material.BEETROOT;
            case COCOA -> Material.COCOA_BEANS;
            case SWEET_BERRY_BUSH -> Material.SWEET_BERRIES;
            case KELP_PLANT -> Material.KELP;
            default -> material;
        };

        this.checkAboveBlock(event.getPlayer(), event.getBlock(), filter);

        this.handleCrops(
                event.getPlayer(),
                event.getBlock().getBlockData(),
                event.getBlock().getDrops(),
                filter
        );
    }
}
