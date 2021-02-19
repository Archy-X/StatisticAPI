package com.archyx.statisticapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.UUID;

public class FileUtil {

    @Nullable
    public static JsonObject getStatisticFile(UUID playerId) {
        // Get file to read from
        File statFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
        File statFile = new File(statFolder, playerId + ".json");
        // Convert to Json
        Gson gson = new Gson();
        try {
            try (Reader reader = new FileReader(statFile)) {
                return gson.fromJson(reader, JsonObject.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
