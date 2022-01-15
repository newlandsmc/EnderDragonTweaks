package com.semivanilla.enderdragontweaks.task;

import com.google.common.collect.Lists;
import com.semivanilla.enderdragontweaks.config.Config;
import com.semivanilla.enderdragontweaks.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.phys.AABB;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DragonSpawnTask extends BukkitRunnable {

	private JavaPlugin plugin;
	private World world;

    public DragonSpawnTask(JavaPlugin plugin, World world) {
        this.plugin = plugin;
		this.world = world;
    }
    
    public void startTask() {
		if (!Util.isValidWorld(world)) return;
    	runTaskLater(plugin, Config.respawnDelay);
    }
    
	@Override
    public void run() {
		DragonBattle dragonBattle = world.getEnderDragonBattle();
		if (dragonBattle == null) return;

		EnderDragon dragon = dragonBattle.getEnderDragon();
		if (dragon != null) {
			cancel();
			return;
		}

		ServerLevel level = ((CraftWorld) world).getHandle();
		EndDragonFight endDragonFight = level.dragonFight();
		if (endDragonFight == null) return;
		if (endDragonFight.respawnStage != null) return;
		BlockPos blockPos = endDragonFight.portalLocation;
		if (blockPos == null) {
			endDragonFight.spawnExitPortal(true);
			blockPos = endDragonFight.portalLocation;
		}
		BlockPos blockPos2 = blockPos.above(1);

		if (!hasEndCrystals(level, blockPos2)) { // only add the endcrystals if they aren't spawned in yet
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				BlockPos blockPos3 = blockPos2.relative(direction, 2);
				EndCrystal entityendercrystal = new EndCrystal(level, blockPos3.getX() + 0.5D, blockPos3.getY(), blockPos3.getZ() + 0.5D);
				entityendercrystal.setShowBottom(false);
				level.addFreshEntity(entityendercrystal);
			}
		}
		endDragonFight.tryRespawn();

		if (!Config.enderDragonSpawned.isBlank())
			Bukkit.broadcast(Util.parseMiniMessage(Config.enderDragonSpawned, null));

	}

	private boolean hasEndCrystals(Level level, BlockPos blockPos) {

		for(Direction direction : Direction.Plane.HORIZONTAL) {
			List<EndCrystal> list2 = level.getEntitiesOfClass(EndCrystal.class, new AABB(blockPos.relative(direction, 2)));
			if (list2.isEmpty()) {
				return false;
			}
		}
		return true;
	}

}