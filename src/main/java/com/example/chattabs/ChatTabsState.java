package com.example.chattabs;

import java.util.ArrayList;
import java.util.List;

public final class ChatTabsState {
    private static final List<TabDefinition> TABS = new ArrayList<>();
    private static int activeIndex = 0;

    private ChatTabsState() {}

    public static void bootstrap() {
        if (!TABS.isEmpty()) return;
        TABS.add(new TabDefinition("Все", "§a", MessageRule.any()));
        TABS.add(new TabDefinition("Глобал", "§b", MessageRule.containsAny("[G]", "global", "чат")));
        TABS.add(new TabDefinition("ЛС", "§d", MessageRule.regexAny("(?i)whispers to you", "(?i)msg from", "(?i)msg to", "(?i)личк", "(?i)лс")));
        TABS.add(new TabDefinition("Пати", "§6", MessageRule.regexAny("(?i)party", "(?i)пати", "(?i)group")));
        TABS.add(new TabDefinition("Торговля", "§e", MessageRule.regexAny("(?i)\\b(buy|sell|wts|wtb|trade|auction|market|shop)\\b", "(?i)купл", "(?i)продам", "(?i)рынок")));
        TABS.add(new TabDefinition("Система", "§7", MessageRule.regexAny("(?i)joined the game", "(?i)left the game", "(?i)achievement", "(?i)систем", "(?i)подключился", "(?i)вышел")));
    }

    public static List<TabDefinition> getTabs() {
        return TABS;
    }

    public static int getActiveIndex() {
        return Math.max(0, Math.min(activeIndex, TABS.size() - 1));
    }

    public static void setActiveIndex(int index) {
        activeIndex = Math.max(0, Math.min(index, TABS.size() - 1));
    }

    public static void nextTab() {
        if (TABS.isEmpty()) return;
        activeIndex = (getActiveIndex() + 1) % TABS.size();
    }

    public static void previousTab() {
        if (TABS.isEmpty()) return;
        activeIndex = (getActiveIndex() - 1 + TABS.size()) % TABS.size();
    }

    public static boolean accepts(String plainText) {
        if (TABS.isEmpty()) return true;
        return TABS.get(getActiveIndex()).rule().matches(plainText);
    }
}
