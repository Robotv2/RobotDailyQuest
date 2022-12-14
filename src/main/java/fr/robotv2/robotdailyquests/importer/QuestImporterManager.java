package fr.robotv2.robotdailyquests.importer;

import com.google.common.collect.ImmutableMap;
import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.importer.impl.OdailyQuestImport;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class QuestImporterManager {

    private final RobotDailyQuest instance;
    private final Map<ImportPlugin, AbstractQuestImporter> importers = new ImmutableMap.Builder<ImportPlugin, AbstractQuestImporter>()
            .put(ImportPlugin.ODAILYQUEST, new OdailyQuestImport())
            .build();

    public QuestImporterManager(RobotDailyQuest instance) {
        this.instance = instance;
    }

    public enum ImportPlugin {
        ODAILYQUEST,
        ;
    }

    public boolean startImport(ImportPlugin plugin) {

        final AbstractQuestImporter importer = importers.get(plugin);

        if(importer == null) {
            throw new IllegalArgumentException("can't find plugin: " + plugin);
        }

        if(importer.getPlugin() == null) {
            return false;
        }

        final List<AbstractQuestImporter.QuestImport> imports = importer.startImport();
        imports.stream()
                .filter(quest -> this.instance.getQuestManager().fromId(quest.id()) == null)
                .forEach(this::toQuestFile);

        try {
            this.instance.getQuestFile().save();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private void toQuestFile(AbstractQuestImporter.QuestImport questImport) {

        FileConfiguration configuration = this.instance.getQuestFile().getConfiguration();
        final String prefix = "quests." + questImport.id() + ".";

        configuration.set(prefix + "name", questImport.name());
        configuration.set(prefix + "menu_item", questImport.material().name());
        configuration.set(prefix + "description", questImport.description());
        configuration.set(prefix + "quest_type", questImport.type().name());
        configuration.set(prefix + "required_targets", questImport.requirement().getTargets());
        configuration.set(prefix + "required_amount", questImport.requirement().getAmount());
        configuration.set(prefix + "quest_reset_delay", questImport.delay().name());
    }
}
