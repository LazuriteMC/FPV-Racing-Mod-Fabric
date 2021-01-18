package dev.lazurite.fpvracing.mixin.common.math;

import dev.lazurite.fpvracing.common.entity.QuadcopterEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Deque;

/**
 * This mixin prevents the screen from rotating along with the player's
 * yaw and pitch values while the player is wearing activated goggles.
 */
@Mixin(MatrixStack.class)
public abstract class MatrixStackMixin {
    @Shadow @Final private Deque<MatrixStack.Entry> stack;

    @Inject(at = @At("HEAD"), method = "multiply", cancellable = true)
    public void multiply(Quaternion quaternion, CallbackInfo info) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Quaternion yaw = Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F);
        Quaternion pitch = Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch());

        if (camera.getFocusedEntity() instanceof QuadcopterEntity  && (quaternion.equals(yaw) || quaternion.equals(pitch))) {
            MatrixStack.Entry entry = stack.getLast();

            entry.getModel().multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
            entry.getNormal().multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));

            entry.getModel().multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(0));
            entry.getNormal().multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(0));

            info.cancel();
        }
    }
}
