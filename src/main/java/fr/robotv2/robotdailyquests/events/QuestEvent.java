package fr.robotv2.robotdailyquests.events;

import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class QuestEvent extends Event {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final ActiveQuest quest;
    public QuestEvent(@NotNull ActiveQuest quest) {
        this.quest = quest;
    }

    public ActiveQuest getQuest() {
        return quest;
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
