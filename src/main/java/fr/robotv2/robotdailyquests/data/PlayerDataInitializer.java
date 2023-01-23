package fr.robotv2.robotdailyquests.data;

import com.j256.ormlite.stmt.QueryBuilder;
import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerDataInitializer implements Listener {

    private final RobotDailyQuest instance;
    public PlayerDataInitializer(RobotDailyQuest instance) {
        this.instance = instance;
    }

    private List<ActiveQuest> queryActiveQuests(UUID playerUUID) {

        final OrmData<ActiveQuest, Integer> data = this.instance.getDatabaseManager().getActiveQuestData();

        try {
            final QueryBuilder<ActiveQuest, Integer> query = data.getDao().queryBuilder();
            query.setWhere(query.where().eq("owner", playerUUID));
            return query.query();
        } catch (SQLException e) {
            instance.getLogger().warning("An error occurred while querying quests: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        QuestPlayer data = instance.getDatabaseManager().getPlayerQuestData().get(player.getUniqueId());

        if(data == null) {
            data = new QuestPlayer(player.getUniqueId());
        }

        for(QuestResetDelay delay : QuestResetDelay.VALUES) {

            final List<ActiveQuest> quests = data.getActiveQuests(delay);

            if(quests.isEmpty() || quests.get(0).getNextReset() < System.currentTimeMillis()) {
                this.instance.getResetService().reset(data, delay);
            }
        }

        QuestPlayer.registerQuestPlayer(data);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        instance.getDatabaseManager().savePlayerData(playerUUID);
        QuestPlayer.unregisterQuestPlayer(playerUUID);
    }
}
