package de.tommycodet.aps.command;

import de.tommycodet.aps.AdvancedPlayerSystem;
import de.tommycodet.aps.data.PlayerData;
import de.tommycodet.aps.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the /profile command.
 * Shows player profile information including playtime, kills, deaths, and balance.
 */
public class ProfileCommand implements CommandExecutor {

    private final AdvancedPlayerSystem plugin;

    /**
     * Creates a new ProfileCommand instance.
     *
     * @param plugin The plugin instance
     */
    public ProfileCommand(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessageFormatter().formatError("This command can only be used by players!"));
            return true;
        }

        // Check permission
        if (!player.hasPermission("advancedplayersystem.profile")) {
            player.sendMessage(plugin.getMessageFormatter().formatError("You don't have permission to use this command!"));
            return true;
        }

        PlayerData targetData;
        String targetName;

        if (args.length == 0) {
            // Show own profile
            targetData = plugin.getDataManager().getPlayerData(player.getUniqueId(), player.getName());
            targetName = player.getName();
        } else {
            // Show another player's profile
            targetName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetName);

            if (targetPlayer == null) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("player", targetName);
                player.sendMessage(plugin.getMessageFormatter().getMessage(
                        "profile.player-not-found",
                        "&cPlayer &f{player} &cnot found!",
                        replacements
                ));
                return true;
            }

            targetData = plugin.getDataManager().getPlayerData(targetPlayer.getUniqueId(), targetPlayer.getName());
        }

        // Display profile
        displayProfile(player, targetData, targetName);
        return true;
    }

    /**
     * Displays a player's profile in chat.
     *
     * @param viewer The player viewing the profile
     * @param data The player data to display
     * @param playerName The name of the player whose profile is being viewed
     */
    private void displayProfile(Player viewer, PlayerData data, String playerName) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("player", playerName);

        // Header
        String header = plugin.getMessageFormatter().getMessage(
                "profile.header",
                "&e========== &f{player}'s Profile &e==========",
                replacements
        );
        viewer.sendMessage(plugin.getMessageFormatter().toComponent(header));

        // First join date
        replacements.put("date", DateUtil.formatDate(data.getFirstJoin()));
        String firstJoin = plugin.getMessageFormatter().getMessage(
                "profile.first-join",
                "&eFirst Join: &f{date}",
                replacements
        );
        viewer.sendMessage(plugin.getMessageFormatter().toComponent(firstJoin));

        // Last login
        replacements.put("date", DateUtil.formatDate(data.getLastLogin()));
        String lastLogin = plugin.getMessageFormatter().getMessage(
                "profile.last-login",
                "&eLastLogin: &f{date}",
                replacements
        );
        viewer.sendMessage(plugin.getMessageFormatter().toComponent(lastLogin));

        // Playtime
        replacements.put("time", data.getFormattedPlaytime());
        String playtime = plugin.getMessageFormatter().getMessage(
                "profile.playtime",
                "&ePlaytime: &f{time}",
                replacements
        );
        viewer.sendMessage(plugin.getMessageFormatter().toComponent(playtime));

        // Kills
        replacements.put("kills", String.valueOf(data.getKills()));
        String kills = plugin.getMessageFormatter().getMessage(
                "profile.kills",
                "&eKills: &f{kills}",
                replacements
        );
        viewer.sendMessage(plugin.getMessageFormatter().toComponent(kills));

        // Deaths
        replacements.put("deaths", String.valueOf(data.getDeaths()));
        String deaths = plugin.getMessageFormatter().getMessage(
                "profile.deaths",
                "&eDeaths: &f{deaths}",
                replacements
        );
        viewer.sendMessage(plugin.getMessageFormatter().toComponent(deaths));

        // Balance
        replacements.put("balance", plugin.getEconomyManager().formatBalance(data.getBalance()));
        replacements.put("currency", plugin.getConfigManager().getCurrencyName());
        String balance = plugin.getMessageFormatter().getMessage(
                "profile.balance",
                "&eBalance: &f{balance} {currency}",
                replacements
        );
        viewer.sendMessage(plugin.getMessageFormatter().toComponent(balance));

        // Footer
        String footer = plugin.getMessageFormatter().getMessage(
                "profile.footer",
                "&e================================"
        );
        viewer.sendMessage(plugin.getMessageFormatter().toComponent(footer));
    }
}
