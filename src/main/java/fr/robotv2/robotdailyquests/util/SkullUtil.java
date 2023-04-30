package fr.robotv2.robotdailyquests.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SkullUtil {

    private SkullUtil() { }

    private final static Map<UUID, ItemStack> heads = new HashMap<>();

    public static ItemStack getSkull(UUID playerUUID) {

        if(heads.containsKey(playerUUID)) {
            return heads.get(playerUUID);
        }

        final ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
        final SkullMeta meta = (SkullMeta) Objects.requireNonNull(stack.getItemMeta());
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
        stack.setItemMeta(meta);

        heads.put(playerUUID, stack);
        return stack;
    }

}
