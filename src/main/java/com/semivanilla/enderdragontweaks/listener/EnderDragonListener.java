package com.semivanilla.enderdragontweaks.listener;

import com.google.common.collect.Sets;
import com.semivanilla.enderdragontweaks.config.Config;
import com.semivanilla.enderdragontweaks.task.DragonSpawnTask;
import com.semivanilla.enderdragontweaks.util.Util;
import net.kyori.adventure.text.minimessage.Template;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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
        DragonBattle dragonBattle = dragonEntity.getDragonBattle();
        if (dragonBattle == null) return; // something went wrong.

        if (world.getEnvironment() != World.Environment.THE_END) return;
        event.setDroppedExp(0); // always set the exp to 0
        event.getDrops().clear(); // clear drops as we scatter these on the island.
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
        if(!serverPlayers.isEmpty()) {
            int expPerPlayer = Config.expToSpread / serverPlayers.size();
            for (ServerPlayer serverPlayer : serverPlayers) {
                serverPlayer.giveExperiencePoints(expPerPlayer);
                BlockPos blockPos = serverPlayer.blockPosition();
                for(Direction direction : Direction.Plane.HORIZONTAL) {
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

        // Handle mob drops and randomly spread the items between the EndSpikeFeature
        LootTable lootTable = dragonEntity.getLootTable();
        if (lootTable == null) return;
        Location endPortalLocation = dragonBattle.getEndPortalLocation();
        if (endPortalLocation == null) {
            dragonBattle.generateEndPortal(true);
            endPortalLocation = dragonBattle.getEndPortalLocation();
        }
        Collection<ItemStack> loot = lootTable.populateLoot(new Random(),
                new LootContext.Builder(endPortalLocation).lootedEntity(dragonEntity).build());
        Bukkit.getLogger().info(playerNames.size() + " size");
        List<ItemStack> items = loot.stream().limit(playerNames.size()).toList();
        for (ItemStack itemStack : items) {
            double randI = ThreadLocalRandom.current().nextDouble(0, 26);
            double randX = ThreadLocalRandom.current().nextDouble(-39, 39);
            double randZ = ThreadLocalRandom.current().nextDouble(-39, 39);
            int x = Mth.floor(randX * Math.cos(2.0D * (-Math.PI + 0.15707963267948966D * (double)randI)));
            int z = Mth.floor(randZ * Math.cos(2.0D * (-Math.PI + 0.15707963267948966D * (double)randI)));
            if (x >= -2 && x <= 2) x +=3;
            if (z >= -2 && z <= 2) z +=3;
            Location location = new Location(world, x, 75, z);
            world.dropItem(location, itemStack);
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
