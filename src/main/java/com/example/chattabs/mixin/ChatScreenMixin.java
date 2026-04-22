package com.example.chattabs.mixin;

import com.example.chattabs.ChatTabsRenderer;
import com.example.chattabs.ChatTabsState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void chattabs$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ChatTabsRenderer.render((ChatScreen)(Object)this, context, mouseX, mouseY, delta);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void chattabs$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == 0 && ChatTabsRenderer.mouseClicked(mouseX, mouseY, (ChatScreen)(Object)this)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void chattabs$mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if (verticalAmount > 0) {
            ChatTabsState.previousTab();
            cir.setReturnValue(true);
        } else if (verticalAmount < 0) {
            ChatTabsState.nextTab();
            cir.setReturnValue(true);
        }
    }
}
