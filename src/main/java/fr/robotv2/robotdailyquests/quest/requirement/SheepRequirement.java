package fr.robotv2.robotdailyquests.quest.requirement;

import fr.robotv2.robotdailyquests.quest.Quest;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;

public class SheepRequirement extends QuestRequirement<Sheep> {

    private final DyeColor color;

    public SheepRequirement(Quest quest) {
        super(quest);
        final String colorStr = quest.getSection().getString("required_sheep_color");

        if(colorStr == null) {
            this.color = null;
            return;
        }

        this.color = Enum.valueOf(DyeColor.class, colorStr.toUpperCase());
    }

    @Override
    public boolean isTarget(Sheep sheep) {
        return color != null && sheep.getColor() == color;
    }

    public DyeColor getColor() {
        return color;
    }
}
