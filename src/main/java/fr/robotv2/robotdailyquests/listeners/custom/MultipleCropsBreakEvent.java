package fr.robotv2.robotdailyquests.listeners.custom;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * For now, only detects bamboo and sugarcane breaking.
 */

public class MultipleCropsBreakEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final List<Block> blocks;
    private final Material material;

    public MultipleCropsBreakEvent(@NotNull Player player, Material material, List<Block> blocks) {
        super(player);
        this.blocks = blocks;
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
