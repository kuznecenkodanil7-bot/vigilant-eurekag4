package com.example.chattabs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

public final class ChatTabsRenderer {
    private ChatTabsRenderer() {}

    public static void render(ChatScreen screen, DrawContext context, int mouseX, int mouseY, float delta) {
        var client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return;

        int x = 6;
        int y = Math.max(4, screen.height - 160);

        for (int i = 0; i < ChatTabsState.getTabs().size(); i++) {
            var tab = ChatTabsState.getTabs().get(i);
            boolean active = i == ChatTabsState.getActiveIndex();

            int width = Math.max(46, client.textRenderer.getWidth(tab.title()) + 18);
            int bg = active ? 0xE61B1F2A : 0xB012141A;
            int stroke = active ? 0xFF66E0FF : 0xFF2B313B;
            int textColor = active ? 0xFFF4F7FB : 0xFFB7C0CE;

            drawRoundedRect(context, x, y, x + width, y + 18, bg, stroke);
            context.drawText(client.textRenderer, Text.literal(tab.title()), x + 9, y + 5, textColor, false);

            x += width + 4;
        }
    }

    private static void drawRoundedRect(DrawContext context, int x1, int y1, int x2, int y2, int fill, int border) {
        context.fill(x1, y1, x2, y2, fill);
        context.fill(x1, y1, x2, y1 + 1, border);
        context.fill(x1, y2 - 1, x2, y2, border);
        context.fill(x1, y1, x1 + 1, y2, border);
        context.fill(x2 - 1, y1, x2, y2, border);
    }

    public static boolean mouseClicked(double mouseX, double mouseY, ChatScreen screen) {
        var client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return false;

        int x = 6;
        int y = Math.max(4, screen.height - 160);

        for (int i = 0; i < ChatTabsState.getTabs().size(); i++) {
            var tab = ChatTabsState.getTabs().get(i);
            int width = Math.max(46, client.textRenderer.getWidth(tab.title()) + 18);

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 18) {
                ChatTabsState.setActiveIndex(i);
                return true;
            }

            x += width + 4;
        }
        return false;
    }
}
