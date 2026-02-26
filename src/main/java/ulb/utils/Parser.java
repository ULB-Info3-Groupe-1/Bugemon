package ulb.utils;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Parser {

    public static void parse(String directory) {

        Path dirPath = Paths.get(directory);

        // load attacks
        Path attacksPath = dirPath.resolve("attaques.json");
        AttackList attackList = parseAttacks(attacksPath);

        List<Attack> attacks = attackList.getAttacks();
        Map<String, Attack> attacksMap = new HashMap<>();

        for (Attack a : attacks) {
            attacksMap.put(a.getId(), a);
        }

        // load bugemons
        Path bugemonPath = dirPath.resolve("bugemons.json");
        List<Bugemon> bugemons = parseBugemons(bugemonPath, attacksMap);
    }

    static AttackList parseAttacks(Path fileName) {

        Gson gson = new Gson();

        try (FileReader reader = new FileReader(fileName.toFile())) {

            Type destType = new TypeToken<AttackList>() {
            }.getType();

            AttackList attacks = gson.fromJson(reader, destType);

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

    static List<Bugemon> parseBugemons(Path fileName, Map<String, Attack> attackMap) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Bugemon.class,
                        new BugemonDeserializer(attackMap))
                .create();

        try (FileReader reader = new FileReader(fileName.toFile())) {

            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray bugemonsArray = root.getAsJsonArray("bugemons");

            Type destType = new TypeToken<List<Bugemon>>() {
            }.getType();

            List<Bugemon> bugemons = gson.fromJson(bugemonsArray, destType);

            reader.close();
            return bugemons;

        } catch (Exception e) {
            // TODO: Use of a Logger or external error management ?

            e.printStackTrace();
        }
        return null;
    }

    static void parseObjects(Path fileName) {

    }

    static void parseSkillTree(Path fileName) {

    }
}