package fr.robotv2.robotdailyquests.data.impl;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.events.QuestDoneEvent;
import fr.robotv2.robotdailyquests.events.QuestIncrementEvent;
import fr.robotv2.robotdailyquests.quest.Quest;
import fr.robotv2.robotdailyquests.quest.QuestRewardProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@DatabaseTable(tableName = "robot_quest_loaded")
public class ActiveQuest {

    @DatabaseField(columnName = "player-done", dataType = DataType.SERIALIZABLE)
    private final ArrayList<UUID> playerDone = new ArrayList<>();

    @DatabaseField(columnName = "player-progress", dataType = DataType.SERIALIZABLE)
    private final HashMap<UUID, Integer> playerProgress = new HashMap<>();

    @DatabaseField(columnName = "quest-id", id = true, unique = true)
    private String questId;

    @DatabaseField(columnName = "start-time-stamp")
    private long startTimeStamp;

    @DatabaseField(columnName = "quest-delay-type")
    private QuestResetDelay delay;

    public ActiveQuest() { }

    public ActiveQuest(Quest quest, long startTimeStamp) {
        this.questId = quest.getId();
        this.delay = quest.getDelay();
        this.startTimeStamp = startTimeStamp;
    }

    public long getStartTimeStamp() {
        return this.startTimeStamp;
    }

    public String getQuestId() {
        return this.questId;
    }

    public QuestResetDelay getResetDelay() {
        return this.delay;
    }

    @Nullable
    public Quest getQuest() {
        return RobotDailyQuest.get().getQuestManager().fromId(this.getQuestId());
    }

    public boolean hasBeenDone(UUID playerUUID) {
        return this.playerDone.contains(playerUUID);
    }

    public void done(UUID playerUUID) {
        this.playerDone.add(playerUUID);
    }

    public int getCurrentProgress(UUID playerUUID) {
        return playerProgress.getOrDefault(playerUUID, 0);
    }

    public void setCurrentProgress(Player player, int amount) {
        playerProgress.put(player.getUniqueId(), amount);
    }

    public void incrementCurrentProgress(Player player, int amount) {

        final UUID playerUUID = player.getUniqueId();
        final Quest quest = this.getQuest();

        final int current = this.getCurrentProgress(playerUUID) + amount;
        this.setCurrentProgress(player, current);

        if(quest == null) return;

        Bukkit.getPluginManager().callEvent(new QuestIncrementEvent(player, this, amount));

        if(current >= quest.getRequirement().getAmount()) {

            this.done(playerUUID);
            PlayerQuestData.getData(playerUUID).incrementQuestAchieved(quest);
            new QuestRewardProcessor().process(player, quest);

            Bukkit.getPluginManager().callEvent(new QuestDoneEvent(player, this));
        }
    }

    public boolean hasEnded() {
        final Quest quest = this.getQuest();

        if (quest == null) {
            return true;
        }

        return this.getStartTimeStamp() - System.currentTimeMillis() > quest.getDelay().milli();
    }

    @Override
    public boolean equals(Object object) {

        if(!(object instanceof ActiveQuest activeQuest)) {
            return false;
        }

        return activeQuest.questId.equals(this.questId) &&
                activeQuest.startTimeStamp == this.startTimeStamp;
    }
}
