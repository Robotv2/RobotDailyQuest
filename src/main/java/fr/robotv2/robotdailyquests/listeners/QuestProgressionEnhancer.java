package fr.robotv2.robotdailyquests.listeners;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class QuestProgressionEnhancer implements Listener {

    private final RobotDailyQuest instance;
    public QuestProgressionEnhancer(RobotDailyQuest instance) {
        this.instance = instance;
    }

    public GlitchChecker getGlitchChecker() {
        return this.instance.getGlitchChecker();
    }

    public void increaseProgression(Player player, QuestType type, String target) {
        this.increaseProgression(player, type, target, 1);
    }

    public void increaseProgression(Player player, QuestType type, String target, int amount) {

        final List<ActiveQuest> quests = instance.getQuestManager().getActiveQuests();

        for (ActiveQuest activeQuest : quests) {

            if(activeQuest.hasBeenDone(player.getUniqueId()) || activeQuest.hasEnded()) {
                return;
            }

            final Quest quest = activeQuest.getQuest();

            if (quest == null // Quest doesn't exist ?? (shouldn't happen)
                    || quest.getType() != type // Quest type doesn't match. (another action)
                    || !quest.getRequirement().isTarget(target)) // The target isn't targeted by this quest.
            {
                continue;
            }

            activeQuest.incrementCurrentProgress(player, amount);
        }
    }
}
