package de.tommycodet.aps.listener;

import de.tommycodet.aps.AdvancedPlayerSystem;
import de.tommycodet.aps.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all player-related events.
 * Tracks player joins, quits, deaths, and kills.
 */
public class PlayerListener implements Listener {

    private final AdvancedPlayerSystem plugin;
    private final Map<String, Long> sessionStartTimes;

    /**
     * Creates a new PlayerListener instance.
     *
     * @param plugin The plugin instance
     */
    public PlayerListener(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
        this.sessionStartTimes = new HashMap<>();
    }

    /**
     * Handles player join event.
     * Creates or loads player data and displays join message.
     *
     * @param event The player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Load or create player data
        PlayerData playerData = plugin.getDataManager().getPlayerData(player.getUniqueId(), player.getName());
        playerData.setLastLogin(System.currentTimeMillis());

        // Start session timer
        sessionStartTimes.put(player.getUniqueId().toString(), System.currentTimeMillis());

        // Display join message
        if (plugin.getConfigManager().areMessagesEnabled()) {
            String joinMessage = plugin.getConfigManager().getJoinMessage();
            joinMessage = joinMessage.replace("{player}", player.getName());
            joinMessage = joinMessage.replace("{time}", playerData.getFormattedPlaytime());
            joinMessage = plugin.getMessageFormatter().formatColors(joinMessage);
            event.joinMessage(plugin.getMessageFormatter().toComponent(joinMessage));
        }

        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Player joined: " + player.getName() + " (" + player.getUniqueId() + ")");
        }
    }

    /**
     * Handles player quit event.
     * Saves player data and calculates session playtime.
     *
     * @param event The player quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        // Calculate and save playtime
        if (sessionStartTimes.containsKey(playerUUID)) {
            long sessionDuration = (System.currentTimeMillis() - sessionStartTimes.get(playerUUID)) / 1000;
            PlayerData playerData = plugin.getDataManager().getPlayerData(player.getUniqueId(), player.getName());
            playerData.addPlaytime(sessionDuration);
            plugin.getDataManager().savePlayerData(playerData);
            sessionStartTimes.remove(playerUUID);

            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().info("Player data saved: " + player.getName() + " (Session: " + sessionDuration + "s)");
            }
        }

        // Display leave message
        if (plugin.getConfigManager().areMessagesEnabled()) {
            String leaveMessage = plugin.getConfigManager().getLeaveMessage();
            leaveMessage = leaveMessage.replace("{player}", player.getName());
            leaveMessage = plugin.getMessageFormatter().formatColors(leaveMessage);
            event.quitMessage(plugin.getMessageFormatter().toComponent(leaveMessage));
        }
    }

    /**
     * Handles player death event.
     * Increments death count and tracks killers.
     *
     * @param event The player death event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        PlayerData victimData = plugin.getDataManager().getPlayerData(victim.getUniqueId(), victim.getName());
        victimData.incrementDeaths();

        // Track killer if it was a player
        if (victim.getKiller() != null) {
            Player killer = victim.getKiller();
            PlayerData killerData = plugin.getDataManager().getPlayerData(killer.getUniqueId(), killer.getName());
            killerData.incrementKills();

            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().info(killer.getName() + " killed " + victim.getName());
            }
        }
    }
}
