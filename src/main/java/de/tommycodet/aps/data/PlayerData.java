package de.tommycodet.aps.data;

import java.util.UUID;

/**
 * Represents a player's profile data.
 * Contains all necessary information about a player's account.
 */
public class PlayerData {

    private final UUID uuid;
    private final String playerName;
    private long firstJoin;
    private long lastLogin;
    private long playtime; // in seconds
    private int kills;
    private int deaths;
    private double balance;

    /**
     * Creates a new PlayerData instance.
     *
     * @param uuid The player's UUID
     * @param playerName The player's name
     */
    public PlayerData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.firstJoin = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
        this.playtime = 0;
        this.kills = 0;
        this.deaths = 0;
        this.balance = 0;
    }

    // Getters and Setters

    public UUID getUUID() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(long firstJoin) {
        this.firstJoin = firstJoin;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    /**
     * Adds playtime to the player's total.
     *
     * @param seconds The seconds to add
     */
    public void addPlaytime(long seconds) {
        this.playtime += seconds;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * Increments the player's kill count.
     */
    public void incrementKills() {
        this.kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    /**
     * Increments the player's death count.
     */
    public void incrementDeaths() {
        this.deaths++;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = Math.max(0, balance);
    }

    /**
     * Converts playtime from seconds to a readable format.
     *
     * @return Formatted playtime (e.g., "1d 2h 30m")
     */
    public String getFormattedPlaytime() {
        long seconds = playtime;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;

        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "uuid=" + uuid +
                ", playerName='" + playerName + '\'' +
                ", playtime=" + playtime +
                ", kills=" + kills +
                ", deaths=" + deaths +
                ", balance=" + balance +
                '}';
    }
}
