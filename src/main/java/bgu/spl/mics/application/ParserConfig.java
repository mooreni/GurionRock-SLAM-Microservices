package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ParserConfig {
    public static ApplicationConfig parseConfig(String path) {
        Gson gson = new Gson();
        ApplicationConfig config = null;
        try (FileReader reader = new FileReader(path)) {
            // Define the type for the list
            Type configType = new TypeToken<ApplicationConfig>(){}.getType();

            // Deserialize JSON to list of cloudpoints
            config = gson.fromJson(reader,configType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
}
