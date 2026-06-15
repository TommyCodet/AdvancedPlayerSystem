package de.tommycodet.aps.command;

import de.tommycodet.aps.AdvancedPlayerSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the /aps command.
 * Main admin command for plugin management (reload, help).
 */
public class APSCommand implements CommandExecutor {

    private final AdvancedPlayerSystem plugin;

    /**
     * Creates a new APSCommand instance.
     *
     * @param plugin The plugin instance
     */
    public APSCommand(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check permission
        if (!sender.hasPermission("advancedplayersystem.admin")) {
            sender.sendMessage(plugin.getMessageFormatter().formatError("You don't have permission to use this command!"));
            return true;
        }

        // No arguments - show help
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "reload":
                handleReload(sender);
                break;
            case "help":
                showHelp(sender);
                break;
            default:
                sender.sendMessage(plugin.getMessageFormatter().formatError("Unknown subcommand: " + subcommand));
                sender.sendMessage(plugin.getMessageFormatter().formatInfo("Use /aps help for available commands"));
                break;
        }

        return true;
    }

    /**
     * Handles the /aps reload command.
     *
     * @param sender The command sender
     */
    private void handleReload(CommandSender sender) {
        if (plugin.reloadConfigs()) {
            sender.sendMessage(plugin.getMessageFormatter().getMessage(
                    "admin.reload-success",
                    "&aConfiguration reloaded successfully!"
            ));
        } else {
            sender.sendMessage(plugin.getMessageFormatter().getMessage(
                    "admin.reload-error",
                    "&cError while reloading configuration"
            ));
        }
    }

    /**
     * Shows the help message.
     *
     * @param sender The command sender
     */
    private void showHelp(CommandSender sender) {
        // Header
        sender.sendMessage(plugin.getMessageFormatter().toComponent(
                plugin.getMessageFormatter().getMessage(
                        "admin.help-header",
                        "&e========== &fAdvancedPlayerSystem Help &e=========="
                )
        ));

        // Commands
        sender.sendMessage(plugin.getMessageFormatter().toComponent(
                plugin.getMessageFormatter().getMessage(
                        "admin.help-profile",
                        "&e/profile [player] &f- Shows a player's profile"
                )
        ));

        sender.sendMessage(plugin.getMessageFormatter().toComponent(
                plugin.getMessageFormatter().getMessage(
                        "admin.help-balance",
                        "&e/balance [player] &f- Shows a player's balance"
                )
        ));

        sender.sendMessage(plugin.getMessageFormatter().toComponent(
                plugin.getMessageFormatter().getMessage(
                        "admin.help-pay",
                        "&e/pay <player> <amount> &f- Transfer money to a player"
                )
        ));

        sender.sendMessage(plugin.getMessageFormatter().toComponent(
                plugin.getMessageFormatter().getMessage(
                        "admin.help-reload",
                        "&e/aps reload &f- Reload the configuration"
                )
        ));

        sender.sendMessage(plugin.getMessageFormatter().toComponent(
                plugin.getMessageFormatter().getMessage(
                        "admin.help-help",
                        "&e/aps help &f- Shows this help message"
                )
        ));

        // Footer
        sender.sendMessage(plugin.getMessageFormatter().toComponent(
                plugin.getMessageFormatter().getMessage(
                        "admin.help-footer",
                        "&e================================"
                )
        ));
    }
}
