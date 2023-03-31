package fr.robotv2.robotdailyquests.listeners;

import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.data.impl.ActiveQuest;
import fr.robotv2.robotdailyquests.data.impl.QuestPlayer;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class QuestProgressionEnhancer implements Listener {

    protected final RobotDailyQuest instance;
    public QuestProgressionEnhancer(RobotDailyQuest instance) {
        this.instance = instance;
    }

    public GlitchChecker getGlitchChecker() {
        return this.instance.getGlitchChecker();
    }

    @Contract("_, null -> true")
    private boolean isTarget(@NotNull Quest quest, @Nullable Object target) {

        if(target == null) {
            return true;
        }

        if(target instanceof Villager villager) {
            return new VillagerQuestRequirement(quest).isTarget(villager);
        }

        if(target instanceof Sheep sheep) {
            return new SheepRequirement(quest).isTarget(sheep);
        }

        return new StringQuestRequirement(quest).isTarget(target.toString());
    }

    public void increaseProgression(Player player, QuestType type, EntityType entityType, int amount) {
        this.increaseProgression(player, type, entityType.name(), amount);
    }

    public void increaseProgression(Player player, QuestType type, Material material, int amount) {
        this.increaseProgression(player, type, material.name(), amount);
    }

    public void increaseProgression(Player player, QuestType type, Object target, int amount) {

        if(amount <= 0) {
            return;
        }

        if(this.isInDisabledWorld(player)) {
            return;
        }

        final Collection<ActiveQuest> quests = QuestPlayer.getQuestPlayer(player).getActiveQuests();

        for (ActiveQuest activeQuest : quests) {

            if(activeQuest.isDone() || activeQuest.hasEnded()) {
                continue;
            }

            final Quest quest = activeQuest.getQuest();

            if (quest == null // Quest doesn't exist ?? (shouldn't happen)
                    || quest.getType() != type // Quest type doesn't match. (another action)
                    || !this.isTarget(quest, target)) // The target isn't targeted by this quest.
            {
                continue;
            }

            activeQuest.incrementCurrentProgress(amount);
        }
    }

    private boolean isInDisabledWorld(Player player) {
        return this.instance.getConfig()
                .getStringList("disabled_worlds")
                .contains(player.getWorld().getName());
    }
}
