package fr.robotv2.robotdailyquests.importer.impl;

import fr.robotv2.robotdailyquests.enums.QuestResetDelay;
import fr.robotv2.robotdailyquests.enums.QuestType;
import fr.robotv2.robotdailyquests.importer.AbstractQuestImporter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OdailyQuestImport extends AbstractQuestImporter {

    @Override
    public String getPluginName() {
        return "ODailyQuests";
    }

    @Override
    public List<QuestImport> startImport() {

        final Plugin plugin = this.getPlugin();

        if(plugin == null) {
            return Collections.emptyList();
        }

        final File folder = plugin.getDataFolder();

        if(!folder.exists()) {
            return Collections.emptyList();
        }

        final File questFolder = new File(folder, "quests");
        final File[] questFiles;

        if(!questFolder.exists() || (questFiles = questFolder.listFiles()) == null) {
            return Collections.emptyList();
        }

        final List<QuestImport> result = new ArrayList<>();

        for(File file : questFiles) {

            final String fileName = file.getName();
            final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection questsSection = configuration.getConfigurationSection("quests");

            if (questsSection == null) continue;

            for (String id : questsSection.getKeys(false)) {
                final ConfigurationSection questSection = questsSection.getConfigurationSection(id);

                if (questSection == null) {
                    continue;
                }

                final QuestImport questImport = this.fromSection(fileName.substring(0, fileName.length() - 4), questSection);

                if (questImport == null) {
                    continue;
                }

                result.add(questImport);
            }
        }

        return result;
    }

    @Nullable
    private QuestImport fromSection(String fileName, ConfigurationSection section) {
        final String id = section.getName() + "-" + fileName;

        final String name = section.getString("name");
        final Material material = Material.matchMaterial(section.getString("menu_item", "BOOK"));
        final List<String> description = section.getStringList("description");
        final int requiredAmount = section.getInt("required_amount");

        final QuestResetDelay delay = this.getDelayByFileName(fileName);
        QuestType type;

        try {
            type = QuestType.valueOf(section.getString("quest_type"));
        } catch (IllegalArgumentException e) {
            type = null;
        }

        if(type == null) {
            return null;
        }

        return new QuestImport(id, name, description, material, delay, type, requiredAmount, Collections.emptyList());
    }

    private List<String> getTargets(ConfigurationSection section) {

        final Object single = section.get("required_item", section.get("required_entity"));
        if(single instanceof String) {
            return Collections.singletonList((String) single);
        }

        final List<String> multiples = section.getStringList("required_item");
        multiples.addAll(section.getStringList("required_entity"));

        return multiples;
    }

    private QuestResetDelay getDelayByFileName(final String fileName) {

        if(fileName.contains("easy")) {
            return QuestResetDelay.DAILY;
        } else if(fileName.contains("medium")) {
            return QuestResetDelay.WEEKLY;
        } else if(fileName.contains("hard")) {
            return QuestResetDelay.MONTHLY;
        }

        return QuestResetDelay.DAILY;
    }
}
