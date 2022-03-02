package com.semivanilla.enderdragontweaks.listener;

import com.google.common.collect.Sets;
import com.semivanilla.enderdragontweaks.config.Config;
import com.semivanilla.enderdragontweaks.task.DragonLootTask;
import com.semivanilla.enderdragontweaks.task.DragonSpawnTask;
import com.semivanilla.enderdragontweaks.util.Util;
import net.kyori.adventure.text.minimessage.Template;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnderDragonListener implements Listener {

    private final JavaPlugin instance;
    private static DragonSpawnTask dragonSpawnTask;
    private static DragonLootTask dragonLootTask;

    public EnderDragonListener(JavaPlugin plugin) {
        instance = plugin;
        dragonSpawnTask = null;
        for (World world : Bukkit.getWorlds()) {
            if (!Util.isValidWorld(world)) continue;
            dragonSpawnTask = new DragonSpawnTask(plugin, world);
            dragonSpawnTask.startTask();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon dragonEntity)) return;
        World world = dragonEntity.getWorld();
        Block eggLocation = world.getBlockAt(0, 67, 0);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (eggLocation.getType() == Material.DRAGON_EGG) {
                    eggLocation.setType(Material.AIR);
                }
            }

        }.runTaskLater(instance, 201); //exactly 1 tick after the egg spawns. The client doesn't even see the block).

        DragonBattle dragonBattle = dragonEntity.getDragonBattle();

        if (dragonBattle == null) return; // something went wrong.

        if (!Util.isValidWorld(world)) return;
        event.setDroppedExp(0); // always set the exp to 0
        if (Config.clearDragonDrops) event.getDrops().clear();
        respawnDragon(world);

        ServerLevel level = ((CraftWorld) world).getHandle();
        EndDragonFight endDragonFight = level.dragonFight();
        if (endDragonFight == null) return;
        ServerBossEvent dragonEvent = endDragonFight.dragonEvent;
        Set<ServerPlayer> serverPlayers = Sets.newHashSet(dragonEvent.getPlayers());
        List<String> playerNames = new ArrayList<>();
        Player player = dragonEntity.getKiller();
        if (player != null)
            playerNames.add(player.getName());
        if (!serverPlayers.isEmpty()) {
            int expPerPlayer = Config.expToSpread / serverPlayers.size();
            for (ServerPlayer serverPlayer : serverPlayers) {
                serverPlayer.giveExperiencePoints(expPerPlayer);
                BlockPos blockPos = serverPlayer.blockPosition();
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    BlockPos blockPos2 = blockPos.relative(direction, 1);
                    level.addFreshEntity(new ExperienceOrb(level, blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), 0));
                }
                playerNames.add(serverPlayer.displayName);
            }
        }
        playerNames = playerNames.stream().distinct().collect(Collectors.toList());
        if (!Config.enderDragonKilled.isBlank()) {
            List<Template> templates = new ArrayList<>(List.of(
                    Template.template("players", StringUtils.join(playerNames.stream().distinct().limit(5).collect(Collectors.toList()), ", "))));
            Bukkit.broadcast(Util.parseMiniMessage(Config.enderDragonKilled, templates));
        }

        dragonLootTask = new DragonLootTask(instance, world, playerNames.size());
        dragonLootTask.startTask();
    }

    public void respawnDragon(World world) {
        if (dragonSpawnTask != null && !dragonSpawnTask.isCancelled()) {
            dragonSpawnTask.cancel();
        }
        dragonSpawnTask = new DragonSpawnTask(instance, world);
        dragonSpawnTask.startTask();
    }

}
