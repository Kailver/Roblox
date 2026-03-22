package com.roblox.prisoncore.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class ColorUtil {
    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private ColorUtil() {
    }

    public static Component text(String message) {
        return MINI.deserialize(message);
    }
}
