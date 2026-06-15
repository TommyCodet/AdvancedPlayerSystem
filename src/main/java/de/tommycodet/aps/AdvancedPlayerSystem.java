package de.tommycodet.aps;

import de.tommycodet.aps.command.BalanceCommand;
import de.tommycodet.aps.command.PayCommand;
import de.tommycodet.aps.command.ProfileCommand;
import de.tommycodet.aps.command.APSCommand;
import de.tommycodet.aps.data.DataManager;
import de.tommycodet.aps.listener.PlayerListener;
import de.tommycodet.aps.manager.ConfigManager;
import de.tommycodet.aps.manager.EconomyManager;
import de.tommycodet.aps.util.MessageFormatter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Main plugin class for AdvancedPlayerSystem.
 * Handles plugin initialization, configuration loading, and task scheduling.
 */
public class AdvancedPlayerSystem extends JavaPlugin {

    private static AdvancedPlayerSystem instance;
    private ConfigManager configManager;
    private DataManager dataManager;
    private EconomyManager economyManager;
    private MessageFormatter messageFormatter;
    private BukkitTask saveTask;

    @Override
    public void onEnable() {
        instance = this;

        // Create default config files if they don't exist
        saveDefaultConfig();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Initialize managers
        try {
            this.configManager = new ConfigManager(this);
            this.messageFormatter = new MessageFormatter(this);
            this.dataManager = new DataManager(this);
            this.economyManager = new EconomyManager(this);

            getLogger().info("✓ Managers initialized successfully");
        } catch (Exception e) {
            getLogger().severe("✗ Failed to initialize managers: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands
        registerCommands();
        getLogger().info("✓ Commands registered");

        // Register event listeners
        registerListeners();
        getLogger().info("✓ Event listeners registered");

        // Schedule auto-save task
        scheduleAutoSave();
        getLogger().info("✓ Auto-save task scheduled");

        getLogger().info("========================================");
        getLogger().info("AdvancedPlayerSystem v" + getDescription().getVersion());
        getLogger().info("Successfully enabled!");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        // Cancel tasks
        if (saveTask != null) {
            saveTask.cancel();
        }

        // Save all player data
        if (dataManager != null) {
            dataManager.saveAllData();
            getLogger().info("✓ All player data saved");
        }

        getLogger().info("========================================");
        getLogger().info("AdvancedPlayerSystem disabled");
        getLogger().info("========================================");
    }

    /**
     * Registers all commands for the plugin.
     */
    private void registerCommands() {
        getCommand("profile").setExecutor(new ProfileCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("aps").setExecutor(new APSCommand(this));
    }

    /**
     * Registers all event listeners for the plugin.
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    /**
     * Schedules the auto-save task based on configuration.
     */
    private void scheduleAutoSave() {
        if (!configManager.isAutoSaveEnabled()) {
            getLogger().info("Auto-save is disabled");
            return;
        }

        long saveInterval = configManager.getSaveInterval();
        saveTask = getServer().getScheduler().runTaskTimerAsynchronously(
                this,
                () -> dataManager.saveAllData(),
                saveInterval,
                saveInterval
        );
    }

    /**
     * Gets the plugin instance.
     *
     * @return The AdvancedPlayerSystem instance
     */
    public static AdvancedPlayerSystem getInstance() {
        return instance;
    }

    /**
     * Gets the configuration manager.
     *
     * @return The ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Gets the data manager.
     *
     * @return The DataManager instance
     */
    public DataManager getDataManager() {
        return dataManager;
    }

    /**
     * Gets the economy manager.
     *
     * @return The EconomyManager instance
     */
    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    /**
     * Gets the message formatter.
     *
     * @return The MessageFormatter instance
     */
    public MessageFormatter getMessageFormatter() {
        return messageFormatter;
    }

    /**
     * Reloads all configuration files.
     *
     * @return true if reload was successful, false otherwise
     */
    public boolean reloadConfigs() {
        try {
            reloadConfig();
            configManager.reload();
            messageFormatter.reload();
            getLogger().info("✓ Configurations reloaded successfully");
            return true;
        } catch (Exception e) {
            getLogger().severe("✗ Error reloading configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
