package net.retrohopper.src.utils;

import org.bukkit.ChatColor;

public class ChatUtils {

    public static String chat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}