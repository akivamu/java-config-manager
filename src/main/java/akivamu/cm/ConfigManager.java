package akivamu.cm;

import com.akivamu.jsonmerger.JsonMerger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();
    private static final JsonMerger jsonMerger = new JsonMerger();

    private static JsonObject configObject;

    public static void load(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File is null or not exists");
        }

        try {
            String jsonString = CommonUtil.readFile(file);
            load(jsonString);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read JSON file", e);
        }
    }

    public static void load(String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            throw new IllegalArgumentException("jsonString is empty");
        }

        try {
            JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
            if (configObject == null) {
                configObject = jsonObject;
            } else {
                // Merge
                JsonObject resultObject = jsonMerger.merge(configObject, jsonObject);
                if (resultObject != null) configObject = resultObject;
            }
        } catch (JsonSyntaxException | IllegalStateException e) {
            throw new IllegalArgumentException("Invalid JSON file", e);
        }
    }

    public static JsonObject getObject() {
        return configObject;
    }

    public static <T> T getObject(Class<T> clazz) {
        return gson.fromJson(configObject, clazz);
    }

    public static <T> T getObject(String path, Class<T> clazz) {
        return gson.fromJson(configObject.getAsJsonObject(path), clazz);
    }

    public static void clear() {
        configObject = null;
    }
}
