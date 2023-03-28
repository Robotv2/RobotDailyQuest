package fr.robotv2.robotdailyquests;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import fr.robotv2.robotdailyquests.data.PlayerDataInitializer;
import fr.robotv2.robotdailyquests.data.QuestDatabaseManager;
import fr.robotv2.robotdailyquests.dependencies.PlaceholderClip;
import fr.robotv2.robotdailyquests.dependencies.VaultAPI;
import fr.robotv2.robotdailyquests.listeners.GlitchChecker;
import fr.robotv2.robotdailyquests.listeners.block.BlockBreakListener;
import fr.robotv2.robotdailyquests.listeners.block.BlockPlaceListener;
import fr.robotv2.robotdailyquests.listeners.block.HarvestBlockListener;
import fr.robotv2.robotdailyquests.listeners.entity.*;
import fr.robotv2.robotdailyquests.listeners.item.*;
import fr.robotv2.robotdailyquests.listeners.player.PlayerExpListener;
import fr.robotv2.robotdailyquests.listeners.quest.QuestListener;
import fr.robotv2.robotdailyquests.manager.QuestManager;
import fr.robotv2.robotdailyquests.quest.Quest;
import fr.robotv2.robotdailyquests.ui.GuiHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;

public final class RobotDailyQuest extends JavaPlugin {

    private final QuestManager questManager = new QuestManager();

    private QuestDatabaseManager questDatabaseManager;
    private QuestResetService service;
    private GlitchChecker glitchChecker;
    private QuestSaveTask saveTask;

    private GuiHandler guiHandler;

    public static RobotDailyQuest get() {
        return JavaPlugin.getPlugin(RobotDailyQuest.class);
    }

    @Override
    public void onEnable() {

        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();

        this.loadFiles();
        this.loadListeners();
        this.loadUis();
        this.loadCommandHandler();

        this.loadDatabaseManager();
        this.saveTask = new QuestSaveTask(this.questDatabaseManager);
        this.saveTask.runTaskTimer(this, 60 * 20 * 5, 60 * 20 * 5); // Every 5 minutes.
        this.service = new QuestResetService(this);

        if(VaultAPI.initialize(this)) {
            getLogger().info("Hook to Vault !");
        }

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderClip(this).register();
            getLogger().info("Hook to placeholder API !");
        }
    }

    @Override
    public void onDisable() {
        this.getSaveTask().run();
        if(this.getDatabaseManager() != null) {
            this.getDatabaseManager().close();
        }
    }

    public void onReload() {
        this.reloadConfig();
        this.getQuestManager().clearQuests();
        this.loadFiles();
        this.guiHandler.reloadGuiConfig();
    }

    // <- GETTERS ->

    public QuestManager getQuestManager() {
        return this.questManager;
    }

    public QuestResetService getResetService() {
        return this.service;
    }

    public GlitchChecker getGlitchChecker() {
        return this.glitchChecker;
    }

    public QuestDatabaseManager getDatabaseManager() {
        return this.questDatabaseManager;
    }

    public QuestSaveTask getSaveTask() {
        return this.saveTask;
    }

    public GuiHandler getGuiHandler() {
        return this.guiHandler;
    }

    public void debug(String message, Object... objects) {
        if(this.getConfig().getBoolean("options.debug")) {
            getLogger().info("[DEBUG] " + String.format(message, objects));
        }
    }

    // <- LOADERS ->

    private void loadFiles() {
        this.getConfig()
                .getStringList("quest-files")
                .forEach(this::loadQuests);
    }

    private void loadQuests(String resourcePath) {

        final File file = new File(this.getDataFolder(), resourcePath);

        if(!file.exists()) {
            throw new NullPointerException("file");
        }

        this.loadQuests(YamlConfiguration.loadConfiguration(file));
    }

    private void loadQuests(@NotNull FileConfiguration configuration) {

        final ConfigurationSection section = configuration.getConfigurationSection("quests");

        if(section == null) {
            throw new NullPointerException("section");
        }

        for(String key : section.getKeys(false)) {

            final ConfigurationSection questSection = section.getConfigurationSection(key);

            if(questSection == null) {
                continue;
            }

            try {
                final Quest quest = new Quest(questSection);
                this.getQuestManager().cacheQuest(quest);
            } catch (Exception exception) {
                getLogger().warning("An error occurred while loading quest: " + key);
                getLogger().warning("Error message: " + exception.getMessage());
            }
        }
    }

    private void loadUis() {
        this.guiHandler = new GuiHandler(this);
    }

    private void loadListeners() {
        final PluginManager pm = getServer().getPluginManager();

        // data-initializer
        pm.registerEvents(new PlayerDataInitializer(this), this);

        // glitch-checker
        pm.registerEvents((this.glitchChecker = new GlitchChecker(this)), this);

        // block
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new HarvestBlockListener(this), this);

        // entity
        pm.registerEvents(new EntityBreedListener(this), this);
        pm.registerEvents(new EntityFishListener(this), this);
        pm.registerEvents(new EntityKillListener(this), this);
        pm.registerEvents(new EntityMilkingListener(this), this);
        pm.registerEvents(new EntityShearListener(this), this);
        pm.registerEvents(new EntityTameListener(this), this);

        // item
        pm.registerEvents(new PlayerConsumeListener(this), this);
        pm.registerEvents(new PlayerCookListener(this), this);
        pm.registerEvents(new PlayerCraftListener(this), this);
        pm.registerEvents(new PlayerEnchantItemListener(this), this);
        pm.registerEvents(new PlayerPickupListener(this), this);

        // player

        pm.registerEvents(new PlayerExpListener(this), this);

        // quest
        pm.registerEvents(new QuestListener(this), this);
    }

    private void loadDatabaseManager() {
        try {
            final File file = new File(getDataFolder(), "database.db");
            if(!file.exists()) file.createNewFile();
            final String databaseURL = "jdbc:sqlite:".concat(file.getPath());
            final ConnectionSource connectionSource = new JdbcConnectionSource(databaseURL);
            this.questDatabaseManager = new QuestDatabaseManager(connectionSource);
        } catch (Exception e) {
            getLogger().warning("An error occurred while loading database manager: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadCommandHandler() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        new QuestCommand(handler, this);
    }
}
