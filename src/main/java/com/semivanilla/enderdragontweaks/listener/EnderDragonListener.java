package com.semivanilla.enderdragontweaks.listener;

import com.google.common.collect.Sets;
import com.semivanilla.enderdragontweaks.config.Config;
import com.semivanilla.enderdragontweaks.task.DragonSpawnTask;
import com.semivanilla.enderdragontweaks.util.Util;
import net.kyori.adventure.text.minimessage.Template;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (!(event.getEntity() instanceof EnderDragon dragonEntity)) return;
        World world = dragonEntity.getWorld();

        if (world.getEnvironment() != World.Environment.THE_END) return;
        int exp = event.getDroppedExp();
        event.setDroppedExp(0);
        respawnDragon(world);

        ServerLevel level = ((CraftWorld) world).getHandle();
        EndDragonFight endDragonFight = level.dragonFight();
        if (endDragonFight == null) return;
        ServerBossEvent dragonEvent = endDragonFight.dragonEvent;
        Set<ServerPlayer> serverPlayers = Sets.newHashSet(dragonEvent.getPlayers());
        List<String> playerNames = new ArrayList<>();
        if(!serverPlayers.isEmpty()) {
            int expPerPlayer = exp / serverPlayers.size();
            for (ServerPlayer serverPlayer : serverPlayers) {
                serverPlayer.giveExperiencePoints(expPerPlayer);
                ExperienceOrb.award(level, serverPlayer.position(), 0);
                playerNames.add(serverPlayer.displayName);
            }
        }
        if (!Config.enderDragonKilled.isBlank()) {
            List<Template> templates = new ArrayList<>(List.of(
                    Template.template("players", StringUtils.join(playerNames.stream().limit(5).collect(Collectors.toList()), ", "))));
            Bukkit.broadcast(Util.parseMiniMessage(Config.enderDragonKilled, templates));
        }
    }

    public void respawnDragon(World world) {
        if (dragonSpawnTask != null && !dragonSpawnTask.isCancelled()) {
            dragonSpawnTask.cancel();
        }
        dragonSpawnTask = new DragonSpawnTask(instance, world);
        dragonSpawnTask.startTask();
    }

}
