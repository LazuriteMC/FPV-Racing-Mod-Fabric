package dev.lazurite.fpvracing.mixin.client.render;

import dev.lazurite.fpvracing.server.item.GogglesItem;
import dev.lazurite.fpvracing.server.entity.flyable.QuadcopterEntity;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin class modifies the behavior of the on-screen HUD. For instance,
 * the {@link InGameHudMixin#renderCrosshair(MatrixStack, CallbackInfo)} injection
 * prevents the crosshair from rendering while the player is flying a drone.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;getArmorStack(I)Lnet/minecraft/item/ItemStack;"
            )
    )
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo info) {
    }

    /**
     * This mixin method removes the crosshair from the player's screen whenever
     * they are flying a {@link QuadcopterEntity}.
     * @param matrices the matrix stack
     * @param info required by every mixin injection
     */
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(MatrixStack matrices, CallbackInfo info) {
        if (GogglesItem.isInGoggles()) {
            info.cancel();
        }
    }

    /**
     * This mixin method removes the experience bar from the player's screen
     * when in adventure or survival modes
     * and are flying a {@link QuadcopterEntity}
     * @param matrices the matrix stack
     * @param x the x coordinate
     * @param info required by every mixin injection
     */
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    public void renderExperienceBar(MatrixStack matrices, int x, CallbackInfo info) {
        if (GogglesItem.isInGoggles()) {
            info.cancel();
        }
    }
}
