package com.example.chattabs.mixin;

import com.example.chattabs.ChatTabsState;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void chattabs$init(CallbackInfo ci) {
        int x = 4;
        int y = ChatTabsState.getConfig().buttonsY;
        int width = Math.max(40, ChatTabsState.getConfig().buttonWidth);
        int height = Math.max(16, ChatTabsState.getConfig().buttonHeight);

        for (int i = 0; i < ChatTabsState.getConfig().tabs.size(); i++) {
            final int index = i;
            var tab = ChatTabsState.getConfig().tabs.get(i);
            boolean active = index == ChatTabsState.getActiveTabIndex();

            MutableText label = Text.literal(active ? "[" + tab.name + "]" : tab.name)
                    .formatted(resolveFormatting(tab.color, active));

            ButtonWidget button = ButtonWidget.builder(label, press -> {
                ChatTabsState.setActiveTab(index);
                ((ChatScreen) (Object) this).clearAndInit();
            }).dimensions(x, y, width, height).build();

            this.addDrawableChild(button);
            x += width + ChatTabsState.getConfig().buttonGap;
        }

        if (ChatTabsState.getConfig().showReloadButton) {
            ButtonWidget reload = ButtonWidget.builder(
                    Text.literal("Reload").formatted(Formatting.YELLOW),
                    press -> {
                        ChatTabsState.reloadConfig();
                        ((ChatScreen) (Object) this).clearAndInit();
                    }
            ).dimensions(x, y, Math.max(50, width), height).build();

            this.addDrawableChild(reload);
        }
    }

    @Inject(method = "mouseScrolled(DDDD)Z", at = @At("HEAD"), cancellable = true)
    private void chattabs$mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if (!ChatTabsState.getConfig().scrollToSwitchTabs || verticalAmount == 0) {
            return;
        }

        if (ChatTabsState.getConfig().invertScroll ? verticalAmount < 0 : verticalAmount > 0) {
            ChatTabsState.previousTab();
        } else {
            ChatTabsState.nextTab();
        }

        ((ChatScreen) (Object) this).clearAndInit();
        cir.setReturnValue(true);
    }

    private static Formatting resolveFormatting(String colorName, boolean active) {
        if (colorName == null || colorName.isBlank()) {
            return active ? Formatting.GREEN : Formatting.WHITE;
        }

        try {
            return Formatting.valueOf(colorName.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return active ? Formatting.GREEN : Formatting.WHITE;
        }
    }
}
