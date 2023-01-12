package fr.robotv2.robotdailyquests.quest.requirement;

import fr.robotv2.robotdailyquests.quest.Quest;
import org.bukkit.entity.Villager;

public class VillagerQuestRequirement extends QuestRequirement<Villager> {

    private final Villager.Profession profession;
    private final int professionLevel;

    public VillagerQuestRequirement(Quest quest) {
        super(quest);
        this.profession = Villager.Profession.valueOf(quest.getSection().getString("required_villager_profession"));
        this.professionLevel = quest.getSection().getInt("required_villager_level");
    }

    @Override
    public boolean isTarget(Villager villager) {
        return this.profession == villager.getProfession() &&
                this.professionLevel == villager.getVillagerLevel();
    }
}
