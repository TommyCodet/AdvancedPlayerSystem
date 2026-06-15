package de.tommycodet.aps.gui;

import de.tommycodet.aps.AdvancedPlayerSystem;
import de.tommycodet.aps.data.PlayerData;
import de.tommycodet.aps.util.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates and manages inventory GUIs for player profiles.
 * Provides a visual representation of player data.
 */
public class ProfileGUI {

    private final AdvancedPlayerSystem plugin;

    /**
     * Creates a new ProfileGUI instance.
     *
     * @param plugin The plugin instance
     */
    public ProfileGUI(AdvancedPlayerSystem plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a profile inventory for a player.
     *
     * @param playerData The player data to display
     * @return The created inventory
     */
    public Inventory createProfileInventory(PlayerData playerData) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("player", playerData.getPlayerName());
        String title = plugin.getMessageFormatter().getMessage(
                "gui.profile-title",
                "&e{player}'s Profile",
                replacements
        );
        // Remove color codes for inventory title
        title = title.replace("&e", "").replace("&f", "").replace("&a", "").replace("&c", "");

        Inventory inventory = Bukkit.createInventory(null, 27, title);

        // Set background
        ItemStack background = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, background);
        }

        // Player head (slot 4)
        ItemStack playerHead = createPlayerHead(playerData);
        inventory.setItem(4, playerHead);

        // Playtime (slot 10)
        ItemStack playtimeItem = createItem(
                Material.CLOCK,
                "&ePlaytime",
                "&f" + playerData.getFormattedPlaytime()
        );
        inventory.setItem(10, playtimeItem);

        // Kills (slot 12)
        ItemStack killsItem = createItem(
                Material.DIAMOND_SWORD,
                "&eKills",
                "&f" + playerData.getKills()
        );
        inventory.setItem(12, killsItem);

        // Deaths (slot 14)
        ItemStack deathsItem = createItem(
                Material.BONE,
                "&eDeaths",
                "&f" + playerData.getDeaths()
        );
        inventory.setItem(14, deathsItem);

        // Balance (slot 16)
        ItemStack balanceItem = createItem(
                Material.GOLD_INGOT,
                "&eBalance",
                "&f" + plugin.getEconomyManager().formatBalance(playerData.getBalance()) + " " + plugin.getConfigManager().getCurrencyName()
        );
        inventory.setItem(16, balanceItem);

        // First join (slot 19)
        ItemStack firstJoinItem = createItem(
                Material.CALENDAR,
                "&eFirst Join",
                "&f" + DateUtil.formatDate(playerData.getFirstJoin())
        );
        inventory.setItem(19, firstJoinItem);

        // Last login (slot 21)
        ItemStack lastLoginItem = createItem(
                Material.PAPER,
                "&eLastLogin",
                "&f" + DateUtil.formatDate(playerData.getLastLogin())
        );
        inventory.setItem(21, lastLoginItem);

        // K/D Ratio (slot 23)
        double kdRatio = playerData.getDeaths() > 0 ? (double) playerData.getKills() / playerData.getDeaths() : playerData.getKills();
        ItemStack kdItem = createItem(
                Material.EMERALD,
                "&eK/D Ratio",
                "&f" + String.format("%.2f", kdRatio)
        );
        inventory.setItem(23, kdItem);

        return inventory;
    }

    /**
     * Creates an item stack with a display name and lore.
     *
     * @param material The material of the item
     * @param displayName The display name (supports color codes)
     * @param lore The lore lines (supports color codes)
     * @return The created item stack
     */
    private ItemStack createItem(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Set display name
            String formattedName = plugin.getMessageFormatter().formatColors(displayName);
            meta.displayName(plugin.getMessageFormatter().toComponent(formattedName));

            // Set lore
            List<Component> loreList = new ArrayList<>();
            for (String loreLine : lore) {
                String formattedLore = plugin.getMessageFormatter().formatColors(loreLine);
                loreList.add(plugin.getMessageFormatter().toComponent(formattedLore));
            }
            meta.lore(loreList);

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Creates a player head item.
     *
     * @param playerData The player data
     * @return The player head item
     */
    private ItemStack createPlayerHead(PlayerData playerData) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            meta.setPlayerProfile(Bukkit.createProfile(playerData.getUUID(), playerData.getPlayerName()));
            String formattedName = plugin.getMessageFormatter().formatColors("&e" + playerData.getPlayerName());
            meta.displayName(plugin.getMessageFormatter().toComponent(formattedName));
            head.setItemMeta(meta);
        }

        return head;
    }
}
