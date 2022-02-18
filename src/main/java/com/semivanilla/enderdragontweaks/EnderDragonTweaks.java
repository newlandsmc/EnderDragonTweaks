package com.semivanilla.enderdragontweaks;

import com.semivanilla.enderdragontweaks.command.DragonTweaksCommand;
import com.semivanilla.enderdragontweaks.config.Config;
import com.semivanilla.enderdragontweaks.listener.EnderDragonListener;
import com.semivanilla.lootitems.LootItems;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class EnderDragonTweaks extends JavaPlugin {

    private static JavaPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        Config.init(instance);
        LootItems.bootStrap(new File(this.getDataFolder(), "loot.yml"));
        registerListener(new EnderDragonListener(instance));
        getCommand("enderdragontweaks").setExecutor(new DragonTweaksCommand());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void registerListener(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
