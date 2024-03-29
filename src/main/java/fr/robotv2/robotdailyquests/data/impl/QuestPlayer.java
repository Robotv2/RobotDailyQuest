package fr.robotv2.robotdailyquests.data.impl;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.Serializable;
import java.util.*;

@DatabaseTable(tableName = "robot_quest_player_data")
public class QuestPlayer {

    private final static Comparator<PlayerQuestArchive> ARCHIVE_COMPARATOR = (archive1, archive2) -> {

        if(archive1.timeStamp() == archive2.timeStamp()) {
            return 0;
        }

        return archive1.timeStamp() > archive2.timeStamp() ? -1 : 1;
    };

    public record PlayerQuestArchive(String questId, String questName, long timeStamp) implements Serializable { }

    @DatabaseField(columnName = "uuid", unique = true, id = true)
    private UUID uuid;

    @DatabaseField(columnName = "archives", dataType = DataType.SERIALIZABLE)
    private ArrayList<PlayerQuestArchive> archives = new ArrayList<>();

    private final List<ActiveQuest> quests = new ArrayList<>();

    public QuestPlayer() { }

    public QuestPlayer(UUID uuid) {
        this.uuid = uuid;
        this.archives = new ArrayList<>();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public int getQuestsAchieved() {
        return this.archives.size();
    }

    // <- QUEST RELATED ->

    @UnmodifiableView
    public List<ActiveQuest> getActiveQuests() {
        return Collections.unmodifiableList(this.quests);
    }

    public List<ActiveQuest> getActiveQuests(QuestResetDelay delay) {
        return quests.stream()
                .filter(quest -> quest.getResetDelay() == delay)
                .toList();
    }

    public void addActiveQuest(ActiveQuest activeQuest) {
        this.quests.add(activeQuest);
    }

    public void removeActiveQuest(QuestResetDelay delay) {
        this.quests.removeIf(quest -> quest.getResetDelay() == delay);
    }

    public boolean hasQuest(String questId) {
        return getActiveQuests().stream()
                .anyMatch(quest -> quest.getQuestId().equals(questId));
    }

    // <- ARCHIVES ->

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

    // <- STATIC ->

    private final static Map<UUID, QuestPlayer> datas = new HashMap<>();

    public static Collection<QuestPlayer> getRegistered() {
        return Collections.unmodifiableCollection(datas.values());
    }

    public static void registerQuestPlayer(QuestPlayer data) {
        datas.put(data.getUniqueId(), data);
    }

    public static void unregisterQuestPlayer(UUID playerUUID) {
        datas.remove(playerUUID);
    }

    public static QuestPlayer getQuestPlayer(Player player) {
        return getQuestPlayer(player.getUniqueId());
    }

    public static QuestPlayer getQuestPlayer(UUID playerUUID) {
        return datas.get(playerUUID);
    }
}
