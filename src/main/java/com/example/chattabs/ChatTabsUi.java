package com.example.chattabs;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ChatTabsUi {
    private ChatTabsUi() {
    }

    public static void addButtons(ChatScreen screen) {
        ChatTabsConfig config = ChatTabsState.getConfig();
        int x = 4;
        int y = config.buttonsY;
        int width = Math.max(40, config.buttonWidth);
        int height = Math.max(16, config.buttonHeight);

        for (int i = 0; i < config.tabs.size(); i++) {
            final int index = i;
            ChatTabsConfig.TabDefinition tab = config.tabs.get(i);
            boolean active = index == ChatTabsState.getActiveTabIndex();
            MutableText label = Text.literal(active ? "[" + tab.name + "]" : tab.name)
                    .formatted(resolveFormatting(tab.color, active));

            ButtonWidget button = ButtonWidget.builder(label, press -> {
                        ChatTabsState.setActiveTab(index);
                        screen.clearAndInit();
                    })
                    .dimensions(x, y, width, height)
                    .build();
            screen.addDrawableChild(button);
            x += width + config.buttonGap;
        }

        if (config.showReloadButton) {
            ButtonWidget reload = ButtonWidget.builder(Text.literal("Reload").formatted(Formatting.YELLOW), press -> {
                        ChatTabsState.reloadConfig();
                        screen.clearAndInit();
                    })
                    .dimensions(x + 6, y, 60, height)
                    .build();
            screen.addDrawableChild(reload);
        }
    }

    private static Formatting resolveFormatting(String value, boolean active) {
        Formatting fallback = active ? Formatting.BOLD : Formatting.WHITE;
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            Formatting formatting = Formatting.byName(value);
            return formatting == null ? fallback : formatting;
        } catch (Exception exception) {
            return fallback;
        }
    }
}
