package com.example.chattabs;

import net.fabricmc.api.ClientModInitializer;

public final class ChatTabsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ChatTabsState.bootstrap();
    }
}
