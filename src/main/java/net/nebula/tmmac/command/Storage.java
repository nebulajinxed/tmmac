package net.nebula.tmmac.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Storage {
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Float>>() {}.getType();
    private static final String FILE_NAME = "config/tmmac.json";

    private static Map<String, Float> configValues = new HashMap<>();

    public static void load(MinecraftServer server) {
        File file = new File(server.getRunDirectory().toFile(), FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Map<String, Float> loaded = GSON.fromJson(reader, MAP_TYPE);
            if (loaded != null) {
                configValues = loaded;
                if (!configValues.containsValue("gunrange")) {
                    configValues.put("gunrange", 18.0f);
                }
                if (!configValues.containsValue("kniferange")) {
                    configValues.put("kniferange", 4.0f);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(MinecraftServer server) {
        File file = new File(server.getRunDirectory().toFile(), FILE_NAME);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(configValues, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void set(String key, float value, MinecraftServer server) {
        configValues.put(key, value);
        save(server);
    }

    public static float load(String key) {
        float value = configValues.getOrDefault(key, 0.0f);
        return value;
    }

    public static Map<String, Float> getAll() {
        return configValues;
    }
}
