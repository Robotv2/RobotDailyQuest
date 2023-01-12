package fr.robotv2.robotdailyquests.quest.requirement;

import fr.robotv2.robotdailyquests.quest.Quest;

import java.util.List;

public class StringQuestRequirement extends QuestRequirement<String> {

    private final List<String> targets;

    public StringQuestRequirement(Quest quest) {
        super(quest);
        this.targets = quest.getSection().getStringList("required_targets");
    }

    @Override
    public boolean isTarget(String target) {
        return this.targets.contains(target);
    }
}
