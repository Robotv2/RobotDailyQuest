package fr.robotv2.robotdailyquests.events;

import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestDoneEvent extends QuestEvent {

    private final Player player;

    public QuestDoneEvent(@NotNull Player player, @NotNull ActiveQuest quest) {
        super(quest);
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }
}
