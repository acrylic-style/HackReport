package xyz.acrylicstyle.hackReport.utils;

import util.CollectionList;

import java.util.List;
import java.util.UUID;

public class ReportDetails {
    private final String name;
    private final UUID uuid;
    private final CollectionList<String> description;

    public ReportDetails(String name, UUID uuid, List<String> description) {
        this.name = name;
        this.uuid = uuid;
        this.description = new CollectionList<>(description);
    }

    public String getName() {
        return name;
    }

    public CollectionList<String> getDescription() {
        return description;
    }

    public UUID getUniqueId() {
        return uuid;
    }
}
