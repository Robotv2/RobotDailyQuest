package fr.robotv2.robotdailyquests.data.impl;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.events.QuestDoneEvent;
import fr.robotv2.robotdailyquests.events.QuestIncrementEvent;
import fr.robotv2.robotdailyquests.quest.Quest;
import fr.robotv2.robotdailyquests.quest.QuestRewardProcessor;
import fr.robotv2.robotdailyquests.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@DatabaseTable(tableName = "robot_quest_loaded")
public class ActiveQuest implements java.io.Serializable {

    @DatabaseField(columnName = "id", unique = true, generatedId = true)
    private int id;

    @DatabaseField(columnName = "owner")
    private UUID owner;

    @DatabaseField(columnName = "quest-id")
    private String questId;

    @DatabaseField(columnName = "start-time-stamp")
    private long startTimeStamp;

    @DatabaseField(columnName = "quest-delay-type")
    private QuestResetDelay delay;

    @DatabaseField(columnName = "quest-done")
    private boolean done;

    @DatabaseField(columnName = "player-progress")
    private int progress = 0;

    @DatabaseField(columnName = "next-reset")
    private long nextReset;

    public ActiveQuest() { }

    public ActiveQuest(UUID owner, Quest quest, long startTimeStamp) {
        this.owner = owner;
        this.questId = quest.getId();
        this.delay = quest.getDelay();
        this.startTimeStamp = startTimeStamp;
        this.nextReset = quest.getDelay().nextResetToEpochMilli();
        RobotDailyQuest.get().debug("New Quest - %s | %s",
                quest.getDelay(),
                DateUtil.getDateFormatted(this.nextReset)
        );
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

    public UUID getOwner() {
        return this.owner;
    }

    @Nullable
    public Quest getQuest() {
        return RobotDailyQuest.get().getQuestManager().fromId(this.getQuestId());
    }

    public boolean isDone() {
        return this.done;
    }

    public void done() {
        this.done = true;
    }

    public int getCurrentProgress() {
        return this.progress;
    }

    public void setCurrentProgress(int amount) {
        this.progress = amount;
    }

    public void incrementCurrentProgress(int amount) {

        final Player player = Bukkit.getPlayer(this.getOwner());

        if(player == null || !player.isOnline()) {
            throw new IllegalStateException("The player you're incrementing progress must be online !");
        }

        final Quest quest = this.getQuest();
        this.setCurrentProgress(this.getCurrentProgress() + amount);

        if(quest == null) return;

        Bukkit.getPluginManager().callEvent(new QuestIncrementEvent(player, this, amount));

        if(this.getCurrentProgress() >= quest.getRequiredAmount()) {

            this.done();
            QuestPlayer.getQuestPlayer(this.getOwner()).incrementQuestAchieved(quest);
            new QuestRewardProcessor().process(player, quest);

            Bukkit.getPluginManager().callEvent(new QuestDoneEvent(player, this));
        }
    }

    public long getNextReset() {
        return this.nextReset;
    }

    public boolean hasEnded() {
        return System.currentTimeMillis() > this.nextReset;
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
