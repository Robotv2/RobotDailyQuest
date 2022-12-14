package fr.robotv2.robotdailyquests.importer;

import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.quest.QuestRequirement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.List;

public abstract class AbstractQuestImporter {

    public record QuestImport(String id, String name, List<String> description, Material material,
                              QuestResetDelay delay, QuestType type, QuestRequirement requirement, List<String> rewards) { }

    public abstract String getPluginName();
    public abstract List<QuestImport> startImport();

    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(this.getPluginName());
    }
}
