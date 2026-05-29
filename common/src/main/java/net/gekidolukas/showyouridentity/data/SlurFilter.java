package net.gekidolukas.showyouridentity.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.platform.Platform;
import net.gekidolukas.showyouridentity.SYIConfig;
import net.gekidolukas.showyouridentity.SYIMod;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SlurFilter {

    private static final Set<String> BANNED_WORDS = new HashSet<>();
    private static final String FILE_NAME = "slurs_filter.txt";


    public static void initialize() {
        BANNED_WORDS.clear();

        Path configDir = Platform.getConfigFolder();
        Path filterFile = configDir.resolve(FILE_NAME);

        try {
            if (!Files.exists(filterFile)) {
                SYIMod.LOGGER.info("slurs_filter.txt not found, trying to download now...");

                Files.createDirectories(configDir);

                String jsonContent = downloadJsonFromInternet(SYIConfig.slur_filter_url);

                if (jsonContent != null && !jsonContent.isEmpty()) {
                    StringBuilder generatedContent = new StringBuilder();
                    generatedContent.append("# =================================================================\n");
                    generatedContent.append("# AUTOMATICALLY GENERATED SLUR-FILTER (categories: Racism, Queerphobia, Hateful Ideologies)\n");
                    generatedContent.append("# Data source loaded from: awdev1/better-profane-words (GitHub)\n");
                    generatedContent.append("# =================================================================\n\n");

                    JsonArray wordsArray = JsonParser.parseString(jsonContent).getAsJsonArray();

                    for (JsonElement element : wordsArray) {
                        JsonObject wordObj = element.getAsJsonObject();


                        if (wordObj.has("categories")) {
                            JsonArray categoriesArray = wordObj.getAsJsonArray("categories");

                            boolean shouldBlock = false;
                            for (JsonElement catElement : categoriesArray) {
                                String category = catElement.getAsString();
                                if (category.equals("slur_racial") ||
                                        category.equals("slur_gender") ||
                                        category.equals("hateful_ideology")) {
                                    shouldBlock = true;
                                    break;
                                }
                            }

                            if (shouldBlock) {
                                String word = wordObj.get("word").getAsString();
                                generatedContent.append(word).append("\n");
                            }
                        }
                    }

                    Files.writeString(filterFile, generatedContent.toString());
                    SYIMod.LOGGER.info("slurs_filter.txt successfully generated and saved in config directory");
                } else {
                    Files.createFile(filterFile);
                    Files.writeString(filterFile, "# Download failed. Please restart server or type in words yourself.\n");
                    SYIMod.LOGGER.error("Slur filter list could not be loaded. Empty file is created.");
                }
            }

            List<String> lines = Files.readAllLines(filterFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    BANNED_WORDS.add(normalizeText(line));
                }
            }

        } catch (Exception e) {
            SYIMod.LOGGER.error("Critical Error in SlurFilter: "+ e.getMessage());
        }
    }



    /**
     * Checks if a word contains a slur
     */
    public static boolean isSevereSlur(String input) {
        if (input == null || input.isEmpty()) return false;

        String normalizedInput = normalizeString(input);

        for (String banned : BANNED_WORDS) {
            if (normalizedInput.contains(banned)) {
                return true;
            }
        }
        return false;
    }

    public static int filterSize() {
        return BANNED_WORDS.size();
    }

    public static boolean addSlur(String word) {
        if (word == null || word.trim().isEmpty()) return false;

        String cleanWord = word.trim().toLowerCase();
        String normalizedWord = normalizeText(cleanWord);

        if (BANNED_WORDS.contains(normalizedWord)) {
            return false;
        }

        Path configDir = Platform.getConfigFolder();
        Path filterFile = configDir.resolve(FILE_NAME);

        try {
            if (!Files.exists(filterFile)) {
                initialize();
            }

            String lineToAdd = cleanWord + "\n";
            Files.writeString(filterFile, lineToAdd, StandardCharsets.UTF_8, StandardOpenOption.APPEND);

            BANNED_WORDS.add(normalizedWord);
            return true;

        } catch (IOException e) {
            SYIMod.LOGGER.error("Error writing into the slurs_filter.txt: " + e.getMessage());
            return false;
        }
    }

    private static String downloadJsonFromInternet(String urlStr) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                SYIMod.LOGGER.error("Server for loading slur filter responded with: "+ response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            SYIMod.LOGGER.error("Network Error while trying to access slur filter: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cleans up strings to check better.
     */
    private static String normalizeString(String input) {
        String text = input.toLowerCase();

        text = text.replace("1", "i")
                .replace("!", "i")
                .replace("3", "e")
                .replace("4", "a")
                .replace("@", "a")
                .replace("0", "o")
                .replace("7", "t")
                .replace("5", "s")
                .replace("$", "s")
                .replace("v", "u");

        text = text.replaceAll("[^a-zäöüß]", "");

        return text;
    }

    private static String normalizeText(String input) {
        String text = input.toLowerCase();
        text = text.replace("1", "i").replace("!", "i").replace("3", "e")
                .replace("4", "a").replace("@", "a").replace("0", "o")
                .replace("7", "t").replace("5", "s").replace("$", "s")
                .replace("v", "u");
        return text.replaceAll("[^a-zäöüß]", "");
    }

}
