package fr.robotv2.robotdailyquests.quest.requirement;

import fr.robotv2.robotdailyquests.quest.Quest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;

public class VillagerQuestRequirement extends QuestRequirement<Villager> {

    private Villager.Profession profession;
    private final int professionLevel;

    public VillagerQuestRequirement(Quest quest) {
        super(quest);
        this.professionLevel = quest.getSection().getInt("required_villager_level", 0);

        try {
            this.profession = Enum.valueOf(Villager.Profession.class, Objects.requireNonNull(quest.getSection().getString("required_villager_profession")));
        } catch (IllegalArgumentException | NullPointerException exception) {
            this.profession = null;
        }
    }

    @Override
    public boolean isTarget(Villager villager) {
        if(this.profession == null && this.professionLevel == 0) {
            return new StringQuestRequirement(this.getQuest()).isTarget(EntityType.VILLAGER.name());
        } else if(this.profession == null) {
            return this.professionLevel >= villager.getVillagerLevel();
        } else if(this.professionLevel == 0) {
            return this.profession == villager.getProfession();
        } else {
            return this.professionLevel >= villager.getVillagerLevel() || this.profession == villager.getProfession();
        }
    }
}
