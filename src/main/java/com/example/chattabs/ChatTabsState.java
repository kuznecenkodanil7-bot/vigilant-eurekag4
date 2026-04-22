package com.example.chattabs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class ChatTabsState {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<StoredMessage> MESSAGES = new ArrayList<>();
    private static ChatTabsConfig config;
    private static int activeTabIndex = 0;
    private static boolean replaying = false;

    private ChatTabsState() {
    }

    public static void init() {
        reloadConfig();
        loadUiState();
        ensureActiveIndexIsValid();
    }

    public static void reloadConfig() {
        config = loadConfig();
        if (config.tabs.isEmpty()) {
            config.tabs.add(ChatTabsConfig.createDefault().tabs.get(0));
        }
        ensureActiveIndexIsValid();
        applyCurrentTab();
    }

    public static ChatTabsConfig loadConfig() {
        Path path = getConfigPath();
        try {
            Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                ChatTabsConfig created = ChatTabsConfig.createDefault();
                try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                    GSON.toJson(created, writer);
                }
                return created;
            }
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                ChatTabsConfig loaded = GSON.fromJson(reader, ChatTabsConfig.class);
                return loaded == null ? ChatTabsConfig.createDefault() : loaded;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            return ChatTabsConfig.createDefault();
        }
    }

    public static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("chattabs.json");
    }

    public static Path getUiStatePath() {
        return FabricLoader.getInstance().getConfigDir().resolve("chattabs-state.json");
    }

    public static void captureMessage(Text text, MessageIndicator indicator) {
        if (text == null) {
            return;
        }
        String raw = text.getString();
        if (raw == null || raw.isBlank()) {
            return;
        }
        MESSAGES.add(new StoredMessage(text.copy(), raw, MessageKind.detect(raw, indicator)));
        int max = Math.max(200, getConfig().maxStoredMessages);
        while (MESSAGES.size() > max) {
            MESSAGES.remove(0);
        }
    }

    public static List<StoredMessage> getMessages() {
        return Collections.unmodifiableList(MESSAGES);
    }

    public static ChatTabsConfig getConfig() {
        if (config == null) {
            config = ChatTabsConfig.createDefault();
        }
        return config;
    }

    public static ChatTabsConfig.TabDefinition getActiveTab() {
        return getConfig().tabs.get(activeTabIndex);
    }

    public static int getActiveTabIndex() {
        return activeTabIndex;
    }

    public static void setActiveTab(int index) {
        if (index < 0 || index >= getConfig().tabs.size()) {
            return;
        }
        activeTabIndex = index;
        saveUiState();
        applyCurrentTab();
    }

    public static void nextTab() {
        activeTabIndex = (activeTabIndex + 1) % getConfig().tabs.size();
        saveUiState();
        applyCurrentTab();
    }

    public static void previousTab() {
        activeTabIndex = (activeTabIndex - 1 + getConfig().tabs.size()) % getConfig().tabs.size();
        saveUiState();
        applyCurrentTab();
    }

    public static boolean isReplaying() {
        return replaying;
    }

    public static void applyCurrentTab() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.inGameHud == null || client.inGameHud.getChatHud() == null) {
            return;
        }

        ChatHud chatHud = client.inGameHud.getChatHud();
        replaying = true;
        try {
            chatHud.clear(false);
            ChatTabsConfig.TabDefinition tab = getActiveTab();
            for (StoredMessage stored : MESSAGES) {
                if (tab.matches(stored)) {
                    chatHud.addMessage(stored.text());
                }
            }
        } finally {
            replaying = false;
        }
    }

    public static String describeActiveTab() {
        ChatTabsConfig.TabDefinition tab = getActiveTab();
        if (getConfig().showIndexInFeedback) {
            return "[" + activeTabIndex + "] " + tab.name;
        }
        return tab.name;
    }

    private static void ensureActiveIndexIsValid() {
        if (getConfig().tabs.isEmpty()) {
            activeTabIndex = 0;
            return;
        }
        if (activeTabIndex < 0 || activeTabIndex >= getConfig().tabs.size()) {
            activeTabIndex = 0;
        }
    }

    private static void loadUiState() {
        Path path = getUiStatePath();
        if (Files.notExists(path)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            UiState state = GSON.fromJson(reader, UiState.class);
            if (state != null) {
                activeTabIndex = Math.max(0, state.activeTabIndex);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void saveUiState() {
        Path path = getUiStatePath();
        try {
            Files.createDirectories(path.getParent());
            UiState state = new UiState();
            state.activeTabIndex = activeTabIndex;
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(state, writer);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static final class UiState {
        int activeTabIndex = 0;
    }

    public record StoredMessage(Text text, String rawText, MessageKind kind) {
    }

    public enum MessageKind {
        ALL,
        CHAT,
        PRIVATE,
        PARTY,
        TRADE,
        SYSTEM,
        GAME_INFO,
        GLOBAL,
        UNKNOWN;

        public boolean matches(String configValue) {
            String normalized = configValue.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
            try {
                return this == MessageKind.valueOf(normalized) || this == ALL;
            } catch (IllegalArgumentException exception) {
                return false;
            }
        }

        public static MessageKind detect(String raw, MessageIndicator indicator) {
            String lower = raw.toLowerCase(Locale.ROOT);

            if (lower.contains("whispers to you") || lower.contains("msg from") || lower.contains("msg to")
                    || lower.contains("tell from") || lower.contains("tell to")
                    || lower.contains("личное") || lower.contains("шепчет")) {
                return PRIVATE;
            }
            if (lower.contains("[party]") || lower.contains("[p]") || lower.contains("[группа]")) {
                return PARTY;
            }
            if (lower.matches(".*\\b(buy|sell|wts|wtb|trade|auction|market|shop|продам|куплю|обмен|магазин|аукцион)\\b.*")) {
                return TRADE;
            }
            if (lower.startsWith("[") && lower.contains("]")) {
                return GLOBAL;
            }

            if (indicator != null) {
                String indicatorText = indicator.toString().toLowerCase(Locale.ROOT);
                if (indicatorText.contains("system")) {
                    return SYSTEM;
                }
                if (indicatorText.contains("game") || indicatorText.contains("overlay")) {
                    return GAME_INFO;
                }
            }

            if (lower.contains("joined the game") || lower.contains("left the game")
                    || lower.contains("achievement") || lower.contains("advancement")
                    || lower.contains("достижение") || lower.contains("вышел") || lower.contains("зашел")) {
                return SYSTEM;
            }

            return CHAT;
        }
    }
}
