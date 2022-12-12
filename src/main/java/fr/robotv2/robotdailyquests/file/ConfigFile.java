package fr.robotv2.robotdailyquests.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigFile {

    private final Plugin plugin;
    private final String fileName;

    private File file;
    private FileConfiguration configuration;

    public ConfigFile(Plugin plugin, String fileName, boolean setup) {
        this.plugin = plugin;
        this.fileName = fileName;
        if(setup) {
            this.setup();
        }
    }

    public void setup() {

        if(file == null) {
            this.file = new File(plugin.getDataFolder(), fileName + ".yml");
        }

        this.plugin.saveResource(fileName + ".yml", false);
    }

    public void save() throws IOException {

        if(file == null || configuration == null) {
            return;
        }

        this.configuration.save(this.file);
    }

    public FileConfiguration getConfiguration() {

        if(this.configuration == null) {
            reload();
        }

        return this.configuration;
    }

    public void reload() {

        if(this.file == null) {
            file = new File(plugin.getDataFolder(), fileName + ".yml");
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        final InputStream defaultStream = plugin.getResource(fileName + ".yml");

        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.configuration.setDefaults(defaultConfig);
        }
    }
}
