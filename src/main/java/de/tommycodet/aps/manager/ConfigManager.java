package de.tommycodet.aps.manager;

import de.tommycodet.aps.AdvancedPlayerSystem;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages configuration settings for the AdvancedPlayerSystem plugin.
 * Handles all config.yml related operations.
 */
public class ConfigManager {

    private final AdvancedPlayerSystem plugin;
    private FileConfiguration config;

    /**
     * Creates a new ConfigManager instance.
     *
     * @param plugin The plugin instance
     */
    public ConfigManager(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    /**
     * Reloads the configuration from disk.
     */
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    /**
     * Gets the starting balance for new players.
     *
     * @return The starting balance
     */
    public double getStartingBalance() {
        return config.getDouble("economy.starting-balance", 1000.0);
    }

    /**
     * Gets the maximum balance a player can have.
     *
     * @return The maximum balance
     */
    public double getMaxBalance() {
        return config.getDouble("economy.max-balance", 1000000.0);
    }

    /**
     * Gets the minimum transaction amount.
     *
     * @return The minimum transaction amount
     */
    public double getMinTransaction() {
        return config.getDouble("economy.min-transaction", 0.01);
    }

    /**
     * Gets the currency name.
     *
     * @return The currency name
     */
    public String getCurrencyName() {
        return config.getString("economy.currency-name", "Coins");
    }

    /**
     * Gets the currency symbol.
     *
     * @return The currency symbol
     */
    public String getCurrencySymbol() {
        return config.getString("economy.currency-symbol", "💰");
    }

    /**
     * Gets the join message template.
     *
     * @return The join message
     */
    public String getJoinMessage() {
        return config.getString("messages.join-message", "&a✓ &e{player} &ahas joined the server");
    }

    /**
     * Gets the leave message template.
     *
     * @return The leave message
     */
    public String getLeaveMessage() {
        return config.getString("messages.leave-message", "&c✗ &e{player} &chas left the server");
    }

    /**
     * Checks if join/leave messages are enabled.
     *
     * @return true if messages are enabled, false otherwise
     */
    public boolean areMessagesEnabled() {
        return config.getBoolean("messages.enabled", true);
    }

    /**
     * Gets the profile GUI title template.
     *
     * @return The profile GUI title
     */
    public String getProfileGUITitle() {
        return config.getString("gui.profile-title", "&e{player}'s Profile");
    }

    /**
     * Gets the plugin prefix for messages.
     *
     * @return The plugin prefix
     */
    public String getPrefix() {
        return config.getString("plugin.prefix", "&8[&eAPS&8] &r");
    }

    /**
     * Gets the save interval in ticks.
     *
     * @return The save interval
     */
    public long getSaveInterval() {
        return config.getLong("plugin.save-interval", 1200L);
    }

    /**
     * Checks if auto-save is enabled.
     *
     * @return true if auto-save is enabled, false otherwise
     */
    public boolean isAutoSaveEnabled() {
        return config.getBoolean("plugin.auto-save", true);
    }

    /**
     * Checks if debug mode is enabled.
     *
     * @return true if debug mode is enabled, false otherwise
     */
    public boolean isDebugEnabled() {
        return config.getBoolean("logging.debug", false);
    }

    /**
     * Checks if transaction logging is enabled.
     *
     * @return true if transaction logging is enabled, false otherwise
     */
    public boolean isTransactionLoggingEnabled() {
        return config.getBoolean("logging.log-transactions", true);
    }
}
