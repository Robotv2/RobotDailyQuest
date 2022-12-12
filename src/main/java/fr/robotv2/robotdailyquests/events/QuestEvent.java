package fr.robotv2.robotdailyquests.events;

import fr.robotv2.robotdailyquests.data.impl.LoadedQuest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestEvent extends Event {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final LoadedQuest quest;
    public QuestEvent(@NotNull LoadedQuest quest) {
        this.quest = quest;
    }

    public LoadedQuest getQuest() {
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
