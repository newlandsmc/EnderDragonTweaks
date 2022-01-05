package com.semivanilla.enderdragontweaks;

import com.semivanilla.enderdragontweaks.config.Config;
import com.semivanilla.enderdragontweaks.listener.EnderDragonListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderDragonTweaks extends JavaPlugin {

    private static JavaPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        Config.init(instance);

        registerListener(new EnderDragonListener(instance));
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
