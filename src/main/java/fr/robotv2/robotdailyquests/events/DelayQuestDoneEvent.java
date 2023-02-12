package fr.robotv2.robotdailyquests.events;

import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Called when all the quest in a delay are done.
 */
public class DelayQuestDoneEvent extends QuestEvent {

    private final Player player;

    public DelayQuestDoneEvent(@NotNull Player player, @NotNull ActiveQuest quest) {
        super(quest);
        this.player = player;
    }

    public ActiveQuest getLastQuestDone() {
        return getQuest();
    }

    public Player getPlayer() {
        return player;
    }

    public QuestPlayer getQuestPlayer() {
        return QuestPlayer.getQuestPlayer(player);
    }

    public QuestResetDelay getDelay() {
        return getLastQuestDone().getResetDelay();
    }
}
