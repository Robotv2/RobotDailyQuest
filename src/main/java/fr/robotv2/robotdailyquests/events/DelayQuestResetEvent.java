package fr.robotv2.robotdailyquests.events;

import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

// Called when an automatic reset for every player is triggered.

public class DelayQuestResetEvent extends Event {

    private final static HandlerList HANDLER_LIST = new HandlerList();
    private final QuestResetDelay delay;

    public DelayQuestResetEvent(QuestResetDelay delay) {
        this.delay = delay;
    }

    public QuestResetDelay getDelay() {
        return this.delay;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
