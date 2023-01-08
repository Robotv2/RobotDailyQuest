package fr.robotv2.robotdailyquests.importer;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import fr.robotv2.robotdailyquests.RobotDailyQuest;
import fr.robotv2.robotdailyquests.importer.impl.OdailyQuestImport;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class QuestImporterManager {

    private final RobotDailyQuest instance;
    private final ClassToInstanceMap<AbstractQuestImporter> importers = new ImmutableClassToInstanceMap.Builder<AbstractQuestImporter>()
            .put(OdailyQuestImport.class, new OdailyQuestImport())
            .build();

    public QuestImporterManager(RobotDailyQuest instance) {
        this.instance = instance;
    }

    public enum ImportPlugin {

        ODAILYQUEST(OdailyQuestImport.class),
        ;

        public final Class<OdailyQuestImport> clazz;
        ImportPlugin(Class<OdailyQuestImport> clazz) {
            this.clazz = clazz;
        }
    }

    public boolean startImport(ImportPlugin plugin, String resourcePath) {

        final AbstractQuestImporter importer = importers.get(plugin.clazz);

        if(importer == null) {
            throw new IllegalArgumentException("Can't find plugin: " + plugin);
        }

        if(!resourcePath.endsWith(".yml")) {
            resourcePath = resourcePath.concat(".yml");
        }

        if(importer.getPlugin() == null) {
            return false;
        }

        final List<AbstractQuestImporter.QuestImport> imports = importer.startImport();

        if(imports.isEmpty()) {
            return false; // Couldn't find any quests.
        }

        final File file = new File(this.instance.getDataFolder(), resourcePath);
        final FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        imports.stream()
                .filter(quest -> this.instance.getQuestManager().fromId(quest.id()) == null)
                .forEach(quest -> this.toQuestFile(quest, configuration));

        try {
            configuration.save(file);
        } catch (IOException e) {
            return false; // An error occurred while creating the quest file.
        }

        return true;
    }

    private void toQuestFile(AbstractQuestImporter.QuestImport questImport, FileConfiguration configuration) {

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
