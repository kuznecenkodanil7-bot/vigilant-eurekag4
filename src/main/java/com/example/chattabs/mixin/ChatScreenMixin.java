package com.example.chattabs.mixin;

import com.example.chattabs.ChatTabsState;
import com.example.chattabs.ChatTabsUi;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Inject(method = "init", at = @At("TAIL"))
    private void chattabs$init(CallbackInfo ci) {
        ChatTabsUi.addButtons((ChatScreen) (Object) this);
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
}
