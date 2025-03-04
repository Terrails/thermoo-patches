package com.github.thedeathlycow.thermoo.patches.mixin.client.compat.colorfulhearts.present;

import com.github.thedeathlycow.thermoo.api.client.StatusBarOverlayRenderEvents;
import com.github.thedeathlycow.thermoo.patches.HeartOverlayRecorder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import terrails.colorfulhearts.heart.Heart;
import terrails.colorfulhearts.heart.HeartType;
import terrails.colorfulhearts.render.HeartRenderer;

import java.util.Arrays;

@Mixin(
        value = HeartRenderer.class,
        remap = false
)
@Environment(EnvType.CLIENT)
public class HeartRendererMixin {

    @Inject(
            method = "renderPlayerHearts",
            at = @At(
                    value = "INVOKE",
                    target = "Lterrails/colorfulhearts/heart/Heart;draw(Lnet/minecraft/client/util/math/MatrixStack;IIZZLterrails/colorfulhearts/heart/HeartType;)V",
                    remap = true,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void captureHeartPositions(
            DrawContext guiGraphics,
            PlayerEntity player,
            int x, int y,
            int maxHealth, int currentHealth,
            int displayHealth, int absorption,
            boolean renderHighlight,
            CallbackInfo ci,
            int healthHearts, int displayHealthHearts,
            boolean absorptionSameRow,
            int regenIndex,
            HeartType heartType,
            int index,
            Heart heart,
            int xPos, int yPos,
            boolean highlightHeart
    ) {
        HeartOverlayRecorder.INSTANCE.setHeartPosition(index, xPos, yPos);
    }

    @Inject(
            method = "renderPlayerHearts",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void drawColdHeartOverlayBar(
            DrawContext drawContext,
            PlayerEntity player,
            int x, int y,
            int maxHealth, int currentHealth, int displayHealth, int absorption,
            boolean renderHighlight,
            CallbackInfo ci
    ) {
        StatusBarOverlayRenderEvents.AFTER_HEALTH_BAR.invoker()
                .render(
                        drawContext,
                        player,
                        HeartOverlayRecorder.INSTANCE.getHeartPositions(),
                        displayHealth,
                        20
                );
        Arrays.fill(HeartOverlayRecorder.INSTANCE.getHeartPositions(), null);
    }
}
