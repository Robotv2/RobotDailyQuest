package fr.robotv2.robotdailyquests.data;

import com.j256.ormlite.support.ConnectionSource;
import fr.robotv2.robotdailyquests.data.impl.LoadedQuest;
import fr.robotv2.robotdailyquests.data.impl.PlayerQuestData;

import java.sql.SQLException;
import java.util.UUID;

public class QuestDatabaseManager {

    private final ConnectionSource source;
    private final OrmData<PlayerQuestData, UUID> playerQuestData = new OrmData<>();
    private final OrmData<LoadedQuest, String> loadedQuest = new OrmData<>();

    public QuestDatabaseManager(ConnectionSource source) throws SQLException {
        this.source = source;
        this.playerQuestData.initialize(source, PlayerQuestData.class);
        this.loadedQuest.initialize(source, LoadedQuest.class);
    }

    public void close() {
        source.closeQuietly();
    }

    public OrmData<PlayerQuestData, UUID> getPlayerQuestData() {
        return this.playerQuestData;
    }

    public OrmData<LoadedQuest, String> getLoadedQuest() {
        return loadedQuest;
    }

    public void savePlayerData(UUID playerUUID) {
        final PlayerQuestData data = PlayerQuestData.getData(playerUUID);
        if(data == null) return;
        this.getPlayerQuestData().save(data);
    }

    public void saveLoadedQuest(LoadedQuest loadedQuest) {
        this.getLoadedQuest().save(loadedQuest);
    }
}
