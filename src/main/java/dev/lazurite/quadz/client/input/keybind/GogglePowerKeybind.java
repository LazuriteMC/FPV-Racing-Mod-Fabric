package dev.lazurite.quadz.client.input.keybind;

import dev.lazurite.quadz.Quadz;
import dev.lazurite.quadz.common.state.entity.QuadcopterEntity;
import dev.lazurite.quadz.common.util.Frequency;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class GogglePowerKeybind {
    public static void register() {
        KeyBinding key = new KeyBinding(
                "key." + Quadz.MODID + ".powergoggles",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key." + Quadz.MODID + ".category"
        );

        KeyBindingHelper.registerKeyBinding(key);

        BiConsumer<ClientPlayerEntity, KeyBinding> sendPacket = (player, pressedKey) -> {
            ItemStack hand = player.getMainHandStack();
            ItemStack hat = player.inventory.armor.get(3);
            ItemStack goggles = null;

            if (hand.getItem().equals(Quadz.GOGGLES_ITEM)) {
                goggles = hand;
            } else if (hat.getItem().equals(Quadz.GOGGLES_ITEM)) {
                goggles = hat;
            }

            if (goggles != null) {
                boolean prevPower = goggles.getOrCreateTag().getBoolean("enabled");
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(pressedKey.equals(key) && !prevPower);
                ClientPlayNetworking.send(Quadz.POWER_GOGGLES_C2S, buf);

                for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
                    if (entity instanceof QuadcopterEntity && ((QuadcopterEntity) entity).getFrequency().equals(Frequency.from(goggles)) && !prevPower && key.equals(pressedKey)) {
                        String sneakKey = KeyBindingHelper.getBoundKeyOf(MinecraftClient.getInstance().options.keySneak).getLocalizedText().getString().toUpperCase();
                        String powerKey = key.getBoundKeyLocalizedText().getString().toUpperCase();
                        player.sendMessage(new TranslatableText("message.quadz.goggles_on", sneakKey, powerKey), true);
                        break;
                    }
                }
            }
        };

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (key.wasPressed()) {
                    sendPacket.accept(client.player, key);
                } else if (client.options.keySneak.wasPressed()) {
                    sendPacket.accept(client.player, client.options.keySneak);
                }
            }
        });
    }
}


