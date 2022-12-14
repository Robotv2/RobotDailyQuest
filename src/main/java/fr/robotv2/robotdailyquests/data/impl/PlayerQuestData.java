package fr.robotv2.robotdailyquests.data.impl;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.robotv2.robotdailyquests.quest.Quest;

import java.util.*;

@DatabaseTable(tableName = "robot_quest_player_data")
public class PlayerQuestData {

    private final static Comparator<PlayerQuestArchive> ARCHIVE_COMPARATOR = (archive1, archive2) -> {
        if(archive1.timeStamp() == archive2.timeStamp()) return 0;
        return archive1.timeStamp() > archive2.timeStamp() ? -1 : 1;
    };
    private final static Map<UUID, PlayerQuestData> datas = new HashMap<>();

    public static void registerData(PlayerQuestData data) {
        datas.put(data.getUniqueId(), data);
    }

    public static void unregisterData(UUID playerUUID) {
        datas.remove(playerUUID);
    }

    public static PlayerQuestData getData(UUID playerUUID) {
        return datas.get(playerUUID);
    }

    public record PlayerQuestArchive(String questId, String questName, long timeStamp) implements java.io.Serializable { }

    @DatabaseField(columnName = "uuid", unique = true, id = true)
    private UUID uuid;

    @DatabaseField(columnName = "archives", dataType = DataType.SERIALIZABLE)
    private ArrayList<PlayerQuestArchive> archives = new ArrayList<>();

    public PlayerQuestData() { }

    public PlayerQuestData(UUID uuid) {
        this.uuid = uuid;
        this.archives = new ArrayList<>();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getQuestsAchieved() {
        return this.archives.size();
    }

    public List<PlayerQuestArchive> getArchives() {
        return archives.stream().sorted(ARCHIVE_COMPARATOR).toList();
    }

    /**
     * @return a list of archives sorted by the more recent to the oldest.
     */
    public List<PlayerQuestArchive> getArchives(String questId) {
        return archives.stream()
                .filter(archive -> archive.questId.equalsIgnoreCase(questId))
                .sorted(ARCHIVE_COMPARATOR).toList();
    }

    public void incrementQuestAchieved(Quest quest) {
        this.archives.add(new PlayerQuestArchive(quest.getId(), quest.getName(), System.currentTimeMillis()));
    }
}
