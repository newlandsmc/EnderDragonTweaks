package com.semivanilla.enderdragontweaks.command;

import com.semivanilla.enderdragontweaks.EnderDragonTweaks;
import com.semivanilla.enderdragontweaks.config.Config;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragonTweaksCommand implements CommandExecutor, TabCompleter {

    public static List<String> commands = List.of("Reload", "add");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                Config.init(EnderDragonTweaks.getInstance());
                sender.sendMessage("Configuration reloaded.");
            }
            case "add" -> {
                if (sender instanceof Player player) {
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    Config.addItemStack(itemStack);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
