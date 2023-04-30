package fr.robotv2.robotdailyquests.data;

import com.j256.ormlite.stmt.QueryBuilder;
import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PlayerDataInitializer(RobotDailyQuest instance) implements Listener {

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

    private void loadQuests(@NotNull QuestPlayer data) {
        Objects.requireNonNull(data);

        final List<ActiveQuest> quests = this.queryActiveQuests(data.getUniqueId());

        for (QuestResetDelay delay : QuestResetDelay.VALUES) {

            final List<ActiveQuest> delayQuest = quests.stream().filter(quest -> quest.getResetDelay() == delay).toList();

            if (delayQuest.isEmpty() || delayQuest.get(0).hasEnded()) {
                this.instance.getResetService().reset(data.getUniqueId(), delay);
            } else {
                delayQuest.forEach(data::addActiveQuest);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        QuestPlayer data = instance.getDatabaseManager().getPlayerQuestData().get(player.getUniqueId());

        if (data == null) {
            data = new QuestPlayer(player.getUniqueId());
        }

        QuestPlayer.registerQuestPlayer(data);
        this.loadQuests(data);

        if (data.getActiveQuests().stream().anyMatch(quest -> !quest.isDone())) {
            final String message = ColorUtil.color(this.instance.getConfig().getString("cosmetics.join-message", "&aVous avez des quêtes disponibles."));
            player.sendMessage(message);
        }

        this.instance.debug("Les données du joueur %s ont été chargées avec succès.", player.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        instance.getDatabaseManager().saveData(playerUUID);
        QuestPlayer.unregisterQuestPlayer(playerUUID);
    }
}
