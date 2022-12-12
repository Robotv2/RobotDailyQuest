package fr.robotv2.robotdailyquests.quest;

import fr.robotv2.robotdailyquests.enums.QuestType;

import java.util.List;

public class QuestRequirement {

    private final QuestType type;
    private final List<String> targets;
    private final int amount;

    public QuestRequirement(QuestType type, List<String> targets, int amount) {
        this.type = type;
        this.targets = targets.stream().map(java.lang.String::toUpperCase).toList();
        this.amount = amount;
    }

    public QuestType getType() {
        return this.type;
    }

    public List<String> getTargets() {
        return this.targets;
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean isTarget(String target) {
        return this.targets.contains(target.toUpperCase());
    }
}
