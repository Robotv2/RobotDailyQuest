package fr.robotv2.robotdailyquests.data;

import com.j256.ormlite.support.ConnectionSource;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;

import java.sql.SQLException;
import java.util.UUID;

public class QuestDatabaseManager {

    private final ConnectionSource source;
    private final OrmData<QuestPlayer, UUID> playerQuestData = new OrmData<>();
    private final OrmData<ActiveQuest, Integer> activeQuestData = new OrmData<>();

    public QuestDatabaseManager(ConnectionSource source) throws SQLException {
        this.source = source;
        this.playerQuestData.initialize(source, QuestPlayer.class);
        this.activeQuestData.initialize(source, ActiveQuest.class);
    }

    public void close() {
        source.closeQuietly();
    }

    public OrmData<QuestPlayer, UUID> getPlayerQuestData() {
        return this.playerQuestData;
    }

    public void savePlayerData(UUID playerUUID) {
        final QuestPlayer data = QuestPlayer.getQuestPlayer(playerUUID);

        if(data == null) {
            return;
        }

        for(ActiveQuest quest : data.getActiveQuests()) {
            this.getActiveQuestData().save(quest);
        }

        this.getPlayerQuestData().save(data);
    }

    public OrmData<ActiveQuest, Integer> getActiveQuestData() {
        return activeQuestData;
    }
}
