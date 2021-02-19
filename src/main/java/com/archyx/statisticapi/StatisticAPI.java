package com.archyx.statisticapi;

import com.google.common.base.CaseFormat;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class StatisticAPI {

    /**
     * Gets a general statistic for any player, online or offline. This will not work for statistics
     * that require an entity type or material.
     * @param playerId The UUID of the player
     * @param statistic The statistic to get
     * @return The statistic value, or 0 if not defined or if there was a file error
     * @throws IllegalArgumentException if the statistic provided requires an entity type or material
     */
    public int getStatistic(UUID playerId, Statistic statistic) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
        // Get using Bukkit API if online
        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            return player.getStatistic(statistic);
        }

        // Check if requires entity type or material
        if (hasEntityType(statistic)) {
            throw new IllegalArgumentException("This statistic requires an entity type to be specified!");
        } else if (hasMaterialType(statistic)) {
            throw new IllegalArgumentException("This statistic requires a material to be specified!");
        }

        // Anything below is for offline players
        if (VersionUtil.getVersion() >= 15) {
            // Get using Bukkit API
            return offlinePlayer.getStatistic(statistic);
        } else if (VersionUtil.getVersion() >= 13) {
            // Load from stat file with new format
            JsonObject statFile = FileUtil.getStatisticFile(playerId);
            if (statFile == null) return 0;
            // Get stat from json
            JsonObject root = statFile.getAsJsonObject("stats");
            JsonObject custom = root.getAsJsonObject("minecraft:custom");
            return custom.get(statistic.getKey().toString()).getAsInt();
        } else {
            // Load from stat file with legacy format
            JsonObject statFile = FileUtil.getStatisticFile(playerId);
            if (statFile == null) return 0;
            return statFile.get("stat." + getLegacyStatisticType(statistic)).getAsInt();
        }
    }

    /**
     * Gets a statistic that requires an entity type for any player, online or offline. This will not work for statistics
     * that do not require an entity type, such as general statistics or material statistics.
     * @param playerId The UUID of the player
     * @param statistic The statistic to get
     * @return The statistic value, or 0 if not defined or if there was a file error
     * @throws IllegalArgumentException if the statistic provided does not require an entity type
     */
    public int getStatistic(UUID playerId, Statistic statistic, EntityType entityType) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
        // Get using Bukkit API if online
        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            return player.getStatistic(statistic, entityType);
        }

        // Check if requires entity type
        if (!hasEntityType(statistic)) {
            throw new IllegalArgumentException("This statistic does not requires an entity type to be specified!");
        }

        // Anything below is for offline players
        if (VersionUtil.getVersion() >= 15) {
            // Load using Bukkit API
            return offlinePlayer.getStatistic(statistic, entityType);
        } else if (VersionUtil.getVersion() >= 13) {
            // Load from stat file with new format
            JsonObject statFile = FileUtil.getStatisticFile(playerId);
            if (statFile == null) return 0;
            // Get stat from json
            JsonObject root = statFile.getAsJsonObject("stats");
            String typeNamespace = getStatisticTypeNamespace(statistic);
            JsonObject custom = root.getAsJsonObject("minecraft:" + typeNamespace);
            return custom.get(entityType.getKey().toString()).getAsInt();
        } else {
            // Load from stat file with legacy format
            JsonObject statFile = FileUtil.getStatisticFile(playerId);
            if (statFile == null) return 0;

            String entityName = getLegacyEntityName(entityType);
            if (entityName == null) return 0;
            return statFile.get("stat." + getLegacyStatisticType(statistic) + "." + entityName).getAsInt();
        }
    }

    /**
     * Gets a statistic that requires a material for any player, online or offline. This will not work for statistics
     * that do not require a material, such as general statistics or entity type statistics.
     * @param playerId The UUID of the player
     * @param statistic The statistic to get
     * @return The statistic value, or 0 if not defined or if there was a file error
     * @throws IllegalArgumentException if the statistic provided does not require an entity type
     */
    public int getStatistic(UUID playerId, Statistic statistic, Material material) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
        // Get using Bukkit API if online
        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            return player.getStatistic(statistic, material);
        }

        // Check if requires entity type
        if (!hasMaterialType(statistic)) {
            throw new IllegalArgumentException("This statistic does not requires a material type to be specified!");
        }

        // Anything below is for offline players
        if (VersionUtil.getVersion() >= 15) {
            // Load using Bukkit API
            return offlinePlayer.getStatistic(statistic, material);
        } else if (VersionUtil.getVersion() >= 13) {
            // Load from stat file with new format
            JsonObject statFile = FileUtil.getStatisticFile(playerId);
            if (statFile == null) return 0;
            // Get stat from json
            JsonObject root = statFile.getAsJsonObject("stats");
            String typeNamespace = getStatisticTypeNamespace(statistic);
            JsonObject custom = root.getAsJsonObject("minecraft:" + typeNamespace);
            return custom.get(material.getKey().toString()).getAsInt();
        } else {
            // Load from stat file with legacy format
            JsonObject statFile = FileUtil.getStatisticFile(playerId);
            if (statFile == null) return 0;
            return statFile.get("stat." + getLegacyStatisticType(statistic) + ".minecraft." + getLegacyMaterialName(material)).getAsInt();
        }
    }

    public String getStatisticTypeNamespace(Statistic stat) {
        switch (stat) {
            case MINE_BLOCK:
                return "mined";
            case BREAK_ITEM:
                return "broken";
            case CRAFT_ITEM:
                return "crafted";
            case USE_ITEM:
                return "used";
            case PICKUP:
                return "picked_up";
            case DROP:
                return "dropped";
            case KILL_ENTITY:
                return "killed";
            case ENTITY_KILLED_BY:
                return "killed_by";
            default:
                return "custom";
        }
    }

    public String getLegacyStatisticType(Statistic stat) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, stat.toString());
    }

    /**
     * Gets whether a statistic requires a material sub type
     * @param stat The statistic to check
     * @return true if the statistic has material sub types, false if not
     */
    public boolean hasMaterialType(Statistic stat) {
        switch (stat) {
            case MINE_BLOCK:
            case BREAK_ITEM:
            case CRAFT_ITEM:
            case USE_ITEM:
            case PICKUP:
            case DROP:
                return true;
            default:
                return false;
        }
    }

    /**
     * Gets whether a statistic requires an entity type sub type
     * @param stat The statistic to check
     * @return true if the statistic has entity type sub types, false if not
     */
    public boolean hasEntityType(Statistic stat) {
        switch (stat) {
            case KILL_ENTITY:
            case ENTITY_KILLED_BY:
                return true;
            default:
                return false;
        }
    }

    private String getLegacyEntityName(EntityType entityType) {
        Class<? extends Entity> entityClass = entityType.getEntityClass();
        if (entityClass == null) return null;
        return entityClass.getName().substring(entityClass.getName().lastIndexOf(".") + 1);
    }

    public String getLegacyMaterialName(Material material) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, material.toString());
    }

}
