package utils;

import models.*;
import java.util.List;
import java.io.IOException;
import java.io.FileReader;
import java.lang.reflect.*;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Parser {

    static List<Attack> parseAttacks(String fileName) {

        Gson gson = new Gson();

        try (FileReader reader = new FileReader(fileName)) {

            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray attacksArray = root.getAsJsonArray("attaques");

            Type destType = new TypeToken<List<Attack>>(){}.getType();

            List<Attack> attacks = gson.fromJson(attacksArray, destType);

            reader.close();

            return attacks;

        } catch (Exception e) {
            // TODO: Use of a Logger or external error management ?
            String errorMessage = "Error when trying to open %s";
            String errorOutput = String.format(errorMessage, fileName);
            System.out.println(errorOutput);
        }
        return null;
    }

    static void parseBugemons(Path fileName) {

    }

    static void parseObjects(Path fileName) {

    }

    static void parseSkillTree(Path fileName) {

    }
}