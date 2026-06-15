package de.tommycodet.aps.manager;

import de.tommycodet.aps.AdvancedPlayerSystem;
import de.tommycodet.aps.data.PlayerData;
import org.bukkit.Bukkit;

/**
 * Manages the economy system for the plugin.
 * Handles balance operations, transactions, and economy rules.
 */
public class EconomyManager {

    private final AdvancedPlayerSystem plugin;
    private final ConfigManager configManager;

    /**
     * Creates a new EconomyManager instance.
     *
     * @param plugin The plugin instance
     */
    public EconomyManager(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    /**
     * Gets the balance of a player.
     *
     * @param playerData The player data
     * @return The player's balance
     */
    public double getBalance(PlayerData playerData) {
        return playerData.getBalance();
    }

    /**
     * Sets the balance of a player.
     *
     * @param playerData The player data
     * @param amount The new balance
     * @return true if successful, false if amount exceeds max balance
     */
    public boolean setBalance(PlayerData playerData, double amount) {
        if (amount < 0) {
            return false;
        }
        if (amount > configManager.getMaxBalance()) {
            return false;
        }
        playerData.setBalance(amount);
        return true;
    }

    /**
     * Adds money to a player's balance.
     *
     * @param playerData The player data
     * @param amount The amount to add
     * @return true if successful, false if operation would exceed max balance
     */
    public boolean addBalance(PlayerData playerData, double amount) {
        if (amount < 0) {
            return false;
        }
        double newBalance = playerData.getBalance() + amount;
        if (newBalance > configManager.getMaxBalance()) {
            return false;
        }
        playerData.setBalance(newBalance);
        return true;
    }

    /**
     * Removes money from a player's balance.
     *
     * @param playerData The player data
     * @param amount The amount to remove
     * @return true if successful, false if player has insufficient funds
     */
    public boolean removeBalance(PlayerData playerData, double amount) {
        if (amount < 0) {
            return false;
        }
        if (playerData.getBalance() < amount) {
            return false;
        }
        playerData.setBalance(playerData.getBalance() - amount);
        return true;
    }

    /**
     * Transfers money from one player to another.
     *
     * @param from The player sending money
     * @param to The player receiving money
     * @param amount The amount to transfer
     * @return true if successful, false otherwise
     */
    public boolean transfer(PlayerData from, PlayerData to, double amount) {
        // Validate amount
        if (amount <= 0) {
            return false;
        }

        if (amount < configManager.getMinTransaction()) {
            return false;
        }

        // Check if sender has enough balance
        if (from.getBalance() < amount) {
            return false;
        }

        // Check if recipient would exceed max balance
        if (to.getBalance() + amount > configManager.getMaxBalance()) {
            return false;
        }

        // Perform transfer
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        // Log transaction if enabled
        if (configManager.isTransactionLoggingEnabled()) {
            logTransaction(from.getPlayerName(), to.getPlayerName(), amount);
        }

        return true;
    }

    /**
     * Checks if a player has enough balance for a transaction.
     *
     * @param playerData The player data
     * @param amount The amount to check
     * @return true if player has enough balance, false otherwise
     */
    public boolean hasBalance(PlayerData playerData, double amount) {
        return playerData.getBalance() >= amount;
    }

    /**
     * Checks if an amount is valid for transaction.
     *
     * @param amount The amount to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidAmount(double amount) {
        return amount > 0 && amount >= configManager.getMinTransaction();
    }

    /**
     * Formats a balance to a readable string.
     *
     * @param balance The balance to format
     * @return Formatted balance string
     */
    public String formatBalance(double balance) {
        return String.format("%.2f", balance);
    }

    /**
     * Logs a transaction to console (if enabled).
     *
     * @param from The sender's name
     * @param to The recipient's name
     * @param amount The transaction amount
     */
    private void logTransaction(String from, String to, double amount) {
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info(String.format(
                    "Transaction: %s -> %s | Amount: %s %s",
                    from, to, formatBalance(amount), configManager.getCurrencyName()
            ));
        }
    }
}
