package com.example.chattabs.mixin;

import com.example.chattabs.ChatTabsState;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin is a template that may need a tiny signature adjustment depending on Yarn build.
 * It filters chat lines based on the selected tab.
 */
@Mixin(targets = "net.minecraft.client.gui.hud.ChatHud")
public abstract class ChatHudMixin {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void chattabs$filterSimple(Text message, CallbackInfoReturnable<Void> cir) {
        if (message == null) return;
        if (!ChatTabsState.accepts(message.getString())) {
            cir.cancel();
        }
    }
}
