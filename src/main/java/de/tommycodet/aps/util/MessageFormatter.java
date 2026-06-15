package de.tommycodet.aps.util;

import de.tommycodet.aps.AdvancedPlayerSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages message formatting and color codes.
 * Handles loading and formatting of all plugin messages.
 */
public class MessageFormatter {

    private final AdvancedPlayerSystem plugin;
    private YamlConfiguration messagesConfig;
    private final Map<String, String> messageCache;

    /**
     * Creates a new MessageFormatter instance.
     *
     * @param plugin The plugin instance
     */
    public MessageFormatter(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
        this.messageCache = new HashMap<>();
        this.messagesConfig = loadMessagesConfig();
    }

    /**
     * Loads the messages configuration file.
     *
     * @return The loaded configuration
     */
    private YamlConfiguration loadMessagesConfig() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        return YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * Reloads the messages configuration.
     */
    public void reload() {
        this.messagesConfig = loadMessagesConfig();
        this.messageCache.clear();
    }

    /**
     * Gets a message from the configuration.
     *
     * @param key The message key (e.g., "profile.header")
     * @param defaultValue The default value if not found
     * @return The message with color codes formatted
     */
    public String getMessage(String key, String defaultValue) {
        String message = messagesConfig.getString(key, defaultValue);
        return formatColors(message);
    }

    /**
     * Gets a message and replaces placeholders.
     *
     * @param key The message key
     * @param defaultValue The default value if not found
     * @param replacements Map of placeholders to replace
     * @return The formatted message with replacements
     */
    public String getMessage(String key, String defaultValue, Map<String, String> replacements) {
        String message = getMessage(key, defaultValue);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    /**
     * Formats a message with color codes.
     * Supports standard Bukkit color codes (&c, &e, etc.)
     *
     * @param message The message to format
     * @return The formatted message
     */
    public String formatColors(String message) {
        return message.replace("&", "§");
    }

    /**
     * Converts a formatted message to an Adventure Component.
     *
     * @param message The formatted message
     * @return The Adventure Component
     */
    public Component toComponent(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    /**
     * Gets the plugin prefix.
     *
     * @return The formatted prefix
     */
    public String getPrefix() {
        return formatColors(plugin.getConfigManager().getPrefix());
    }

    /**
     * Formats an error message with prefix.
     *
     * @param message The error message
     * @return The formatted error message with prefix
     */
    public String formatError(String message) {
        return getPrefix() + "&c" + message;
    }

    /**
     * Formats a success message with prefix.
     *
     * @param message The success message
     * @return The formatted success message with prefix
     */
    public String formatSuccess(String message) {
        return getPrefix() + "&a" + message;
    }

    /**
     * Formats an info message with prefix.
     *
     * @param message The info message
     * @return The formatted info message with prefix
     */
    public String formatInfo(String message) {
        return getPrefix() + "&e" + message;
    }
}
