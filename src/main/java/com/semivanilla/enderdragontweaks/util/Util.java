package com.semivanilla.enderdragontweaks.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Util {

    public static Component parseMiniMessage(String message, List<Template> templates) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        if (templates == null) {
            return miniMessage.deserialize(message);
        } else {
            return miniMessage.deserialize(message, TemplateResolver.templates(templates));
        }
    }

    public static void sendMiniMessage(CommandSender sender, String message, List<Template> templates) {
        Component component = parseMiniMessage(message, templates);
        sender.sendMessage(component);
    }

}
