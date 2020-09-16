package xyz.acrylicstyle.hackReport.utils;

import java.util.UUID;

public class PlayerInfo {
    private final String name;
    private final UUID uuid;
    private int reports = 0;
    private int kills = 0;
    private int deaths = 0;

    public PlayerInfo(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
    }

    public void increaseReports() {
        this.reports++;
    }

    public void increaseKills() {
        this.kills++;
    }

    public int getKills() {
        return kills;
    }

    public void increaseDeaths() {
        this.deaths++;
    }

    public int getDeaths() {
        return deaths;
    }
}
