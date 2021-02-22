package dev.lazurite.fpvracing.mixin.client.render;

import dev.lazurite.fpvracing.client.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin class modifies the behavior of the entity renderer
 * such that the client player will render even in first-person view.
 * The reason for this is then the player can be seen while flying a drone.
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;",
                    ordinal = 3
            )
    )
    public Entity getFocusedEntity(Camera camera) {
        if (Config.getInstance().shouldRenderPlayer) {
            return camera.getFocusedEntity();
        }

        return client.player;
    }
}
