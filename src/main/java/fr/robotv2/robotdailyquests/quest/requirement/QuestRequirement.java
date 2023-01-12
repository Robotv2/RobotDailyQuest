package fr.robotv2.robotdailyquests.quest.requirement;

import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.quest.Quest;

public abstract class QuestRequirement<T> {

    private final QuestType type;
    private final int amount;

    public QuestRequirement(Quest quest) {
        this.type = quest.getType();
        this.amount = quest.getSection().getInt("required_amount");
    }

    public QuestType getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public abstract boolean isTarget(T t);
}
