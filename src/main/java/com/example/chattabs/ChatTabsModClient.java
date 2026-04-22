package com.example.chattabs;

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
                        .then(ClientCommandManager.literal("next").executes(ctx -> {
                            ChatTabsState.nextTab();
                            ctx.getSource().sendFeedback(Text.literal("[ChatTabs] Следующая вкладка: " + ChatTabsState.getActiveTab().name));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("prev").executes(ctx -> {
                            ChatTabsState.previousTab();
                            ctx.getSource().sendFeedback(Text.literal("[ChatTabs] Предыдущая вкладка: " + ChatTabsState.getActiveTab().name));
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("set").then(ClientCommandManager.argument("index", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    int index = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "index");
                                    ChatTabsState.setActiveTab(index);
                                    ctx.getSource().sendFeedback(Text.literal("[ChatTabs] Активная вкладка: " + ChatTabsState.getActiveTab().name));
                                    return 1;
                                })))
        ));
    }
}
