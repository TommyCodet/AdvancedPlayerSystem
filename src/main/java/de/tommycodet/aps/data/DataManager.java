package de.tommycodet.aps.data;

import de.tommycodet.aps.AdvancedPlayerSystem;
import de.tommycodet.aps.util.DateUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages player data persistence using YAML files.
 * Handles loading and saving of individual player profiles.
 */
public class DataManager {

    private final AdvancedPlayerSystem plugin;
    private final File dataFolder;
    private final Map<UUID, PlayerData> playerDataCache;

    /**
     * Creates a new DataManager instance.
     *
     * @param plugin The plugin instance
     */
    public DataManager(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.playerDataCache = new HashMap<>();

        // Create playerdata folder if it doesn't exist
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    /**
     * Gets or creates player data for a player.
     *
     * @param uuid The player's UUID
     * @param playerName The player's name
     * @return The player's data
     */
    public PlayerData getPlayerData(UUID uuid, String playerName) {
        // Check cache first
        if (playerDataCache.containsKey(uuid)) {
            return playerDataCache.get(uuid);
        }

        // Load from file
        PlayerData data = loadPlayerData(uuid);
        if (data == null) {
            // Create new profile
            data = new PlayerData(uuid, playerName);
            data.setBalance(plugin.getConfigManager().getStartingBalance());
            data.setFirstJoin(System.currentTimeMillis());
            data.setLastLogin(System.currentTimeMillis());
        }

        // Add to cache
        playerDataCache.put(uuid, data);
        return data;
    }

    /**
     * Loads player data from a YAML file.
     *
     * @param uuid The player's UUID
     * @return The player's data, or null if file doesn't exist
     */
    private PlayerData loadPlayerData(UUID uuid) {
        File dataFile = new File(dataFolder, uuid + ".yml");
        if (!dataFile.exists()) {
            return null;
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
            PlayerData data = new PlayerData(uuid, config.getString("name"));
            data.setFirstJoin(config.getLong("first-join", System.currentTimeMillis()));
            data.setLastLogin(config.getLong("last-login", System.currentTimeMillis()));
            data.setPlaytime(config.getLong("playtime", 0));
            data.setKills(config.getInt("kills", 0));
            data.setDeaths(config.getInt("deaths", 0));
            data.setBalance(config.getDouble("balance", plugin.getConfigManager().getStartingBalance()));
            return data;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load player data for " + uuid + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Saves a player's data to a YAML file.
     *
     * @param playerData The player data to save
     */
    public void savePlayerData(PlayerData playerData) {
        File dataFile = new File(dataFolder, playerData.getUUID() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        try {
            config.set("name", playerData.getPlayerName());
            config.set("first-join", playerData.getFirstJoin());
            config.set("last-login", playerData.getLastLogin());
            config.set("playtime", playerData.getPlaytime());
            config.set("kills", playerData.getKills());
            config.set("deaths", playerData.getDeaths());
            config.set("balance", playerData.getBalance());
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player data for " + playerData.getUUID() + ": " + e.getMessage());
        }
    }

    /**
     * Saves all cached player data to disk.
     */
    public void saveAllData() {
        for (PlayerData data : playerDataCache.values()) {
            savePlayerData(data);
        }
    }

    /**
     * Unloads a player's data from cache.
     *
     * @param uuid The player's UUID
     */
    public void unloadPlayerData(UUID uuid) {
        PlayerData data = playerDataCache.remove(uuid);
        if (data != null) {
            savePlayerData(data);
        }
    }

    /**
     * Checks if a player has data saved.
     *
     * @param uuid The player's UUID
     * @return true if data file exists, false otherwise
     */
    public boolean hasPlayerData(UUID uuid) {
        return new File(dataFolder, uuid + ".yml").exists();
    }
}
