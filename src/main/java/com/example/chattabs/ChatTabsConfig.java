package com.example.chattabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ChatTabsConfig {
    public List<TabDefinition> tabs = new ArrayList<>();
    public int maxStoredMessages = 2000;
    public int buttonWidth = 72;
    public int buttonHeight = 18;
    public int buttonGap = 3;
    public int buttonsY = 4;
    public boolean scrollToSwitchTabs = true;
    public boolean invertScroll = false;
    public boolean showReloadButton = true;

    public static ChatTabsConfig createDefault() {
        ChatTabsConfig config = new ChatTabsConfig();

        TabDefinition all = new TabDefinition();
        all.name = "All";
        all.all = true;
        all.color = "white";
        config.tabs.add(all);

        TabDefinition privateTab = new TabDefinition();
        privateTab.name = "PM";
        privateTab.color = "light_purple";
        privateTab.messageTypes.add("private");
        privateTab.regex.add("(?i)whispers to you");
        privateTab.regex.add("(?i)from ");
        privateTab.regex.add("(?i)to ");
        privateTab.regex.add("(?i)msg from");
        privateTab.regex.add("(?i)msg to");
        config.tabs.add(privateTab);

        TabDefinition party = new TabDefinition();
        party.name = "Party";
        party.color = "aqua";
        party.messageTypes.add("party");
        party.contains.add("[Party]");
        party.contains.add("[P]");
        party.contains.add("[Группа]");
        config.tabs.add(party);

        TabDefinition trade = new TabDefinition();
        trade.name = "Trade";
        trade.color = "gold";
        trade.messageTypes.add("trade");
        trade.regex.add("(?i)\\b(buy|sell|wts|wtb|trade|auction|market|shop|продам|куплю|обмен)\\b");
        config.tabs.add(trade);

        TabDefinition system = new TabDefinition();
        system.name = "System";
        system.color = "green";
        system.messageTypes.add("system");
        system.messageTypes.add("game_info");
        config.tabs.add(system);

        return config;
    }

    public static final class TabDefinition {
        public String name = "Tab";
        public boolean all = false;
        public String color = "white";
        public List<String> contains = new ArrayList<>();
        public List<String> regex = new ArrayList<>();
        public List<String> excludesContains = new ArrayList<>();
        public List<String> excludesRegex = new ArrayList<>();
        public List<String> messageTypes = new ArrayList<>();
        public boolean caseSensitive = false;
        public boolean matchAllRules = false;

        public boolean matches(ChatTabsState.StoredMessage message) {
            if (all) {
                return true;
            }

            String rawMessage = message.rawText();
            String source = caseSensitive ? rawMessage : rawMessage.toLowerCase(Locale.ROOT);

            for (String exclude : excludesContains) {
                String rule = caseSensitive ? exclude : exclude.toLowerCase(Locale.ROOT);
                if (source.contains(rule)) {
                    return false;
                }
            }

            for (String regexRule : excludesRegex) {
                if (rawMessage.matches(".*(?:" + regexRule + ").*")) {
                    return false;
                }
            }

            List<Boolean> checks = new ArrayList<>();

            if (!messageTypes.isEmpty()) {
                boolean typeMatched = false;
                for (String type : messageTypes) {
                    if (message.kind().matches(type)) {
                        typeMatched = true;
                        break;
                    }
                }
                checks.add(typeMatched);
            }

            if (!contains.isEmpty()) {
                boolean containsMatched = false;
                for (String containsRule : contains) {
                    String rule = caseSensitive ? containsRule : containsRule.toLowerCase(Locale.ROOT);
                    if (source.contains(rule)) {
                        containsMatched = true;
                        break;
                    }
                }
                checks.add(containsMatched);
            }

            if (!regex.isEmpty()) {
                boolean regexMatched = false;
                for (String regexRule : regex) {
                    if (rawMessage.matches(".*(?:" + regexRule + ").*")) {
                        regexMatched = true;
                        break;
                    }
                }
                checks.add(regexMatched);
            }

            if (checks.isEmpty()) {
                return false;
            }

            if (matchAllRules) {
                for (Boolean check : checks) {
                    if (!check) {
                        return false;
                    }
                }
                return true;
            }

            for (Boolean check : checks) {
                if (check) {
                    return true;
                }
            }
            return false;
        }
    }
}
