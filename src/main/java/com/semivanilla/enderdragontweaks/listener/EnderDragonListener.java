package com.semivanilla.enderdragontweaks.listener;

import com.semivanilla.enderdragontweaks.task.DragonSpawnTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderDragonListener implements Listener {

    private final JavaPlugin instance;
    private static DragonSpawnTask dragonSpawnTask;

    public EnderDragonListener(JavaPlugin plugin) {
        instance = plugin;
        dragonSpawnTask = null;
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() != World.Environment.THE_END) continue;
            dragonSpawnTask = new DragonSpawnTask(plugin, world);
            dragonSpawnTask.startTask();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) return;

        LivingEntity dragonEntity = event.getEntity();
        World world = dragonEntity.getWorld();

        if (world.getEnvironment() != World.Environment.THE_END) return;

        respawnDragon(world);
    }

    public void respawnDragon(World world) {
        if (dragonSpawnTask != null && !dragonSpawnTask.isCancelled()) {
            dragonSpawnTask.cancel();
        }
        dragonSpawnTask = new DragonSpawnTask(instance, world);
        dragonSpawnTask.startTask();
    }

}
