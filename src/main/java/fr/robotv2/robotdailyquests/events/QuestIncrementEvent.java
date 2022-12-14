package fr.robotv2.robotdailyquests.events;

import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.quest.Quest;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestIncrementEvent extends QuestEvent {

    private final Player player;
    private final int amount;

    public QuestIncrementEvent(@NotNull Player player, @NotNull ActiveQuest activeQuest, int amount) {
        super(activeQuest);
        this.player = player;
        this.amount = amount;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public int getAmountAdded() {
        return amount;
    }

    public int getPlayerProgression() {
        return getQuest().getCurrentProgress(player.getUniqueId());
    }

    public int getAmountRequired() {
        final Quest quest = getQuest().getQuest();

        if(quest == null) {
            return -1;
        }

        return quest.getRequirement().getAmount();
    }
}
