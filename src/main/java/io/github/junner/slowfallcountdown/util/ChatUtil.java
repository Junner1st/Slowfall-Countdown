package io.github.junner.slowfallcountdown.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatUtil {
    public static void sendMsg(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (client.world == null || client.inGameHud == null) {
                return;
            }

            client.inGameHud.getChatHud().addMessage(Text.literal(message));
        });
    }
}
