package fr.robotv2.robotdailyquests.listeners;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.quest.Quest;
import fr.robotv2.robotdailyquests.quest.requirement.SheepRequirement;
import fr.robotv2.robotdailyquests.quest.requirement.StringQuestRequirement;
import fr.robotv2.robotdailyquests.quest.requirement.VillagerQuestRequirement;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public abstract class QuestProgressionEnhancer implements Listener {

    private final RobotDailyQuest instance;
    public QuestProgressionEnhancer(RobotDailyQuest instance) {
        this.instance = instance;
    }

    public GlitchChecker getGlitchChecker() {
        return this.instance.getGlitchChecker();
    }

    private boolean isTarget(Quest quest, Object target, Objects... objects) {

        if(target instanceof Villager villager) {
            return new VillagerQuestRequirement(quest).isTarget(villager);
        }

        if(quest.getType() == QuestType.SHEAR && target instanceof Sheep sheep) {
            return new SheepRequirement(quest).isTarget(sheep);
        }

        return new StringQuestRequirement(quest).isTarget(target.toString());
    }

    public void increaseProgression(Player player, QuestType type, EntityType entityType, int amount, Object... objects) {
        this.increaseProgression(player, type, entityType.name(), amount);
    }

    public void increaseProgression(Player player, QuestType type, Material material, int amount, Object... objects) {
        this.increaseProgression(player, type, material.name(), amount);
    }

    public void increaseProgression(Player player, QuestType type, Object target, int amount) {

        final List<ActiveQuest> quests = instance.getQuestManager().getActiveQuests();

        for (ActiveQuest activeQuest : quests) {

            if(activeQuest.hasBeenDone(player.getUniqueId()) || activeQuest.hasEnded()) {
                return;
            }

            final Quest quest = activeQuest.getQuest();

            if (quest == null // Quest doesn't exist ?? (shouldn't happen)
                    || quest.getType() != type // Quest type doesn't match. (another action)
                    || !this.isTarget(quest, target)) // The target isn't targeted by this quest.
            {
                continue;
            }

            activeQuest.incrementCurrentProgress(player, amount);
        }
    }
}
