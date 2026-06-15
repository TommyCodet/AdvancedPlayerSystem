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
 * Handles the /pay command.
 * Transfers money from one player to another.
 */
public class PayCommand implements CommandExecutor {

    private final AdvancedPlayerSystem plugin;

    /**
     * Creates a new PayCommand instance.
     *
     * @param plugin The plugin instance
     */
    public PayCommand(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessageFormatter().formatError("This command can only be used by players!"));
            return true;
        }

        // Check permission
        if (!player.hasPermission("advancedplayersystem.pay")) {
            player.sendMessage(plugin.getMessageFormatter().formatError("You don't have permission to use this command!"));
            return true;
        }

        // Check argument count
        if (args.length < 2) {
            player.sendMessage(plugin.getMessageFormatter().formatError("Usage: /pay <player> <amount>"));
            return true;
        }

        String targetName = args[0];
        String amountStr = args[1];

        // Parse amount
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("input", amountStr);
            player.sendMessage(plugin.getMessageFormatter().getMessage(
                    "general.not-a-number",
                    "&cInvalid number: &f{input}",
                    replacements
            ));
            return true;
        }

        // Get target player
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", targetName);
            player.sendMessage(plugin.getMessageFormatter().getMessage(
                    "pay.player-not-found",
                    "&cPlayer &f{player} &cnot found!",
                    replacements
            ));
            return true;
        }

        // Check if player is paying themselves
        if (player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            player.sendMessage(plugin.getMessageFormatter().getMessage(
                    "pay.cannot-pay-yourself",
                    "&cYou can't pay yourself!"
            ));
            return true;
        }

        // Validate amount
        if (!plugin.getEconomyManager().isValidAmount(amount)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("min", String.valueOf(plugin.getConfigManager().getMinTransaction()));
            replacements.put("currency", plugin.getConfigManager().getCurrencyName());
            player.sendMessage(plugin.getMessageFormatter().getMessage(
                    "pay.min-transaction-error",
                    "&cMinimum transaction amount is &f{min} {currency}",
                    replacements
            ));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(plugin.getMessageFormatter().getMessage(
                    "pay.invalid-amount",
                    "&cInvalid amount! Must be greater than 0"
            ));
            return true;
        }

        // Get player data
        PlayerData senderData = plugin.getDataManager().getPlayerData(player.getUniqueId(), player.getName());
        PlayerData receiverData = plugin.getDataManager().getPlayerData(targetPlayer.getUniqueId(), targetPlayer.getName());

        // Check sender's balance
        if (!plugin.getEconomyManager().hasBalance(senderData, amount)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("currency", plugin.getConfigManager().getCurrencyName());
            replacements.put("needed", plugin.getEconomyManager().formatBalance(amount));
            replacements.put("balance", plugin.getEconomyManager().formatBalance(senderData.getBalance()));
            player.sendMessage(plugin.getMessageFormatter().getMessage(
                    "pay.insufficient-funds",
                    "&cYou don't have enough {currency}! You need &f{needed} &cbut only have &f{balance}",
                    replacements
            ));
            return true;
        }

        // Check receiver's max balance
        if (receiverData.getBalance() + amount > plugin.getConfigManager().getMaxBalance()) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", targetName);
            player.sendMessage(plugin.getMessageFormatter().getMessage(
                    "pay.max-balance-exceeded",
                    "&c{player} &chas reached the maximum balance!",
                    replacements
            ));
            return true;
        }

        // Perform transfer
        if (plugin.getEconomyManager().transfer(senderData, receiverData, amount)) {
            // Send success messages
            Map<String, String> senderReplacements = new HashMap<>();
            senderReplacements.put("amount", plugin.getEconomyManager().formatBalance(amount));
            senderReplacements.put("currency", plugin.getConfigManager().getCurrencyName());
            senderReplacements.put("player", targetName);
            String senderMessage = plugin.getMessageFormatter().getMessage(
                    "pay.payment-sent",
                    "&aYou sent &f{amount} {currency} &ato &f{player}",
                    senderReplacements
            );
            player.sendMessage(plugin.getMessageFormatter().toComponent(senderMessage));

            Map<String, String> receiverReplacements = new HashMap<>();
            receiverReplacements.put("amount", plugin.getEconomyManager().formatBalance(amount));
            receiverReplacements.put("currency", plugin.getConfigManager().getCurrencyName());
            receiverReplacements.put("player", player.getName());
            String receiverMessage = plugin.getMessageFormatter().getMessage(
                    "pay.payment-received",
                    "&aYou received &f{amount} {currency} &afrom &f{player}",
                    receiverReplacements
            );
            targetPlayer.sendMessage(plugin.getMessageFormatter().toComponent(receiverMessage));
        } else {
            player.sendMessage(plugin.getMessageFormatter().formatError("Transaction failed. Please try again."));
        }

        return true;
    }
}
