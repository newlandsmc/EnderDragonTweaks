package com.semivanilla.enderdragontweaks.task;

import com.semivanilla.enderdragontweaks.config.Config;
import com.semivanilla.enderdragontweaks.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonSpawnTask extends BukkitRunnable {

	private JavaPlugin plugin;
	private World world;

    public DragonSpawnTask(JavaPlugin plugin, World world) {
        this.plugin = plugin;
		this.world = world;
    }
    
    public void startTask() {
		if (world.getEnvironment() != World.Environment.THE_END) return; // Don't start if this isn't the end
    	runTaskLater(plugin, Config.respawnDelay);
		if (!Config.enderDragonKilled.isBlank())
			Bukkit.broadcast(Util.parseMiniMessage(Config.enderDragonKilled, null));
    }
    
	@Override
    public void run() {
		DragonBattle dragonBattle = world.getEnderDragonBattle();
		if (dragonBattle == null) return;

		EnderDragon dragon = dragonBattle.getEnderDragon();
		if (dragon != null) {
			Bukkit.getLogger().info("Dragon respawn task canceled as there is a dragon in the world already.");
			cancel();
			return;
		}

		ServerLevel level = ((CraftWorld) world).getHandle();
		EndDragonFight endDragonFight = level.dragonFight();
		if (endDragonFight == null) return;
		BlockPos blockPos = endDragonFight.portalLocation;
		if (blockPos == null) {
			endDragonFight.spawnExitPortal(true);
			blockPos = endDragonFight.portalLocation;
		}
		BlockPos blockPos2 = blockPos.above(1);
		for(Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos blockPos3 = blockPos2.relative(direction, 2);
			EndCrystal entityendercrystal = new EndCrystal(level, blockPos3.getX() + 0.5D, blockPos3.getY(), blockPos3.getZ() + 0.5D);
			entityendercrystal.setShowBottom(false);
			level.addFreshEntity(entityendercrystal);
		}
		endDragonFight.tryRespawn();

		if (!Config.enderDragonSpawned.isBlank())
			Bukkit.broadcast(Util.parseMiniMessage(Config.enderDragonSpawned, null));

	}

}