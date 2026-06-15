package de.tommycodet.aps.command;

import de.tommycodet.aps.AdvancedPlayerSystem;
import de.tommycodet.aps.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the /balance command.
 * Shows a player's current balance.
 */
public class BalanceCommand implements CommandExecutor {

    private final AdvancedPlayerSystem plugin;

    /**
     * Creates a new BalanceCommand instance.
     *
     * @param plugin The plugin instance
     */
    public BalanceCommand(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessageFormatter().formatError("This command can only be used by players!"));
            return true;
        }

        // Check permission
        if (!player.hasPermission("advancedplayersystem.balance")) {
            player.sendMessage(plugin.getMessageFormatter().formatError("You don't have permission to use this command!"));
            return true;
        }

        PlayerData targetData;
        String messageKey;
        String defaultMessage;

        if (args.length == 0) {
            // Show own balance
            targetData = plugin.getDataManager().getPlayerData(player.getUniqueId(), player.getName());
            messageKey = "balance.your-balance";
            defaultMessage = "&eYour Balance: &f{balance} {currency}";
        } else {
            // Show another player's balance
            String targetName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetName);

            if (targetPlayer == null) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("player", targetName);
                player.sendMessage(plugin.getMessageFormatter().getMessage(
                        "balance.player-not-found",
                        "&cPlayer &f{player} &cnot found!",
                        replacements
                ));
                return true;
            }

            targetData = plugin.getDataManager().getPlayerData(targetPlayer.getUniqueId(), targetPlayer.getName());
            messageKey = "balance.player-balance";
            defaultMessage = "&e{player}'s Balance: &f{balance} {currency}";
        }

        // Prepare replacements
        Map<String, String> replacements = new HashMap<>();
        replacements.put("player", targetData.getPlayerName());
        replacements.put("balance", plugin.getEconomyManager().formatBalance(targetData.getBalance()));
        replacements.put("currency", plugin.getConfigManager().getCurrencyName());

        String message = plugin.getMessageFormatter().getMessage(messageKey, defaultMessage, replacements);
        player.sendMessage(plugin.getMessageFormatter().toComponent(message));
        return true;
    }
}
