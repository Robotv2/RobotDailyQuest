package fr.robotv2.robotdailyquests.data;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerDataInitializer implements Listener {

    private final RobotDailyQuest instance;
    public PlayerDataInitializer(RobotDailyQuest instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        PlayerQuestData data = instance.getDatabaseManager().getPlayerQuestData().get(player.getUniqueId());

        if(data == null) {
            data = new PlayerQuestData(player.getUniqueId());
        }

        PlayerQuestData.registerData(data);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        instance.getDatabaseManager().savePlayerData(playerUUID);
        PlayerQuestData.unregisterData(playerUUID);
    }
}
