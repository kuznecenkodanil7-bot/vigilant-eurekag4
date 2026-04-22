package com.example.chattabs;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

public final class ChatTabsModClient implements ClientModInitializer {
    public static final String MOD_ID = "chattabs";

    @Override
    public void onInitializeClient() {
        ChatTabsState.init();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("chattabs")
                        .executes(ctx -> {
                            ChatTabsState.reloadConfig();
                            ctx.getSource().sendFeedback(Text.literal("[ChatTabs] Конфиг перезагружен."));
                            return 1;
                        })
                        .then(ClientCommandManager.literal("reload").executes(ctx -> {
                            ChatTabsState.reloadConfig();
                            ctx.getSource().sendFeedback(Text.literal("[ChatTabs] Конфиг перезагружен."));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("list").executes(ctx -> {
                            StringBuilder result = new StringBuilder("[ChatTabs] Вкладки: ");
                            for (int i = 0; i < ChatTabsState.getConfig().tabs.size(); i++) {
                                if (i > 0) {
                                    result.append(", ");
                                }
                                result.append("[").append(i).append("] ").append(ChatTabsState.getConfig().tabs.get(i).name);
                            }
                            ctx.getSource().sendFeedback(Text.literal(result.toString()));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("next").executes(ctx -> {
                            ChatTabsState.nextTab();
                            ctx.getSource().sendFeedback(Text.literal("[ChatTabs] Следующая вкладка: " + ChatTabsState.describeActiveTab()));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("prev").executes(ctx -> {
                            ChatTabsState.previousTab();
                            ctx.getSource().sendFeedback(Text.literal("[ChatTabs] Предыдущая вкладка: " + ChatTabsState.describeActiveTab()));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("set").then(ClientCommandManager.argument("index", IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    int index = IntegerArgumentType.getInteger(ctx, "index");
                                    ChatTabsState.setActiveTab(index);
                                    ctx.getSource().sendFeedback(Text.literal("[ChatTabs] Активная вкладка: " + ChatTabsState.describeActiveTab()));
                                    return 1;
                                })))
        ));
    }
}
