package com.semivanilla.enderdragontweaks.util;

import com.semivanilla.enderdragontweaks.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Util {

    private static MiniMessage miniMessage;

    private static MiniMessage miniMessage() {
        if (miniMessage == null)
            miniMessage = MiniMessage.miniMessage();
        return miniMessage;
    }

    public static Component parseMiniMessage(String message) {
        return miniMessage().deserialize(message);
    }

    public static Component parseMiniMessage(String message, List<Template> templates) {
        if (templates == null) {
            return miniMessage().deserialize(message);
        } else {
            return miniMessage().deserialize(message, TemplateResolver.templates(templates));
        }
    }

    public static void sendMiniMessage(CommandSender sender, String message, List<Template> templates) {
        Component component = parseMiniMessage(message, templates);
        sender.sendMessage(component);
    }

    public static boolean isValidWorld(World world) {
        if (world.getEnderDragonBattle() == null) return false;
        if (Config.worldName.isBlank()) return true;

        return world.getName().equalsIgnoreCase(Config.worldName);
    }
}
