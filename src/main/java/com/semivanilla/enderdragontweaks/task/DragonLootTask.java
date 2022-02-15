package com.semivanilla.enderdragontweaks.task;

import com.semivanilla.enderdragontweaks.config.Config;
import com.semivanilla.enderdragontweaks.loot.LootItems;
import com.semivanilla.enderdragontweaks.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DragonLootTask extends BukkitRunnable {

	private final JavaPlugin plugin;
	private final World world;
	private final int count;

    public DragonLootTask(JavaPlugin plugin, World world, int count) {
        this.plugin = plugin;
		this.world = world;
		this.count = count;
    }
    
    public void startTask() {
    	runTask(plugin);
    }
    
	@Override
    public void run() {
		// Handle mob drops and randomly spread the items between the EndSpikeFeature
		DragonBattle dragonBattle = world.getEnderDragonBattle();
		if (dragonBattle == null) return;

		Location endPortalLocation = dragonBattle.getEndPortalLocation();
		if (endPortalLocation == null) {
			dragonBattle.generateEndPortal(true);
			endPortalLocation = dragonBattle.getEndPortalLocation();
		}

		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
		int min = Math.min(count, Config.minPlayerCap);
		int max = Math.min(count, Config.maxPlayerCap);
		int drops = min == max ? min : threadLocalRandom.nextInt(min, max);
		List<ItemStack> items = LootItems.generateLoot(drops);
		for (ItemStack itemStack : items) {
			double randI = threadLocalRandom.nextDouble(0, 26);
			double randX = threadLocalRandom.nextDouble(Config.minX, Config.maxX);
			double randZ = threadLocalRandom.nextDouble(Config.minZ, Config.maxZ);
			int x = Mth.floor(randX * Math.cos(2.0D * (-Math.PI + 0.15707963267948966D * (double)randI)));
			int z = Mth.floor(randZ * Math.cos(2.0D * (-Math.PI + 0.15707963267948966D * (double)randI)));
			if (x >= -2 && x <= 2) x +=3;
			if (z >= -2 && z <= 2) z +=3;
			int y = Config.minY == Config.maxY ? Config.minY : threadLocalRandom.nextInt(Config.minY, Config.maxY);
			Location location = new Location(world, x, y, z);
			world.dropItem(location, itemStack);
		}
	}

}