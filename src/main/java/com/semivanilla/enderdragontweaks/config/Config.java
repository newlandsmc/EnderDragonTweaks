package com.semivanilla.enderdragontweaks.config;

import com.google.common.base.Throwables;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Config {

    private static final String HEADER = "";

    private static File CONFIG_FILE;
    public static File CONFIG_PATH;
    public static YamlConfiguration config;

    static int version;

    public static void init(JavaPlugin plugin) {
        CONFIG_PATH = plugin.getDataFolder();
        CONFIG_FILE = new File(CONFIG_PATH, "config.yml");
        config = new YamlConfiguration();
        try {
            config.load(CONFIG_FILE);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load config.yml, please correct your syntax errors", ex);
            Throwables.throwIfUnchecked(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);

        version = getInt("config-version", 1);

        readConfig(Config.class, null);
    }

    static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        Throwables.throwIfUnchecked(ex);
                    } catch (Exception ex) {
                        Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
                    }
                }
            }
        }
        saveConfig();
    }

    static void saveConfig() {
        try {
            config.save(CONFIG_FILE);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    private static void set(String path, Object val) {
        config.addDefault(path, val);
        config.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static long getLong(String path, long def) {
        config.addDefault(path, def);
        return config.getLong(path, config.getLong(path));
    }

    private static <T> List getList(String path, T def) {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    private static List<ItemStack> getItemStackList(String path, List<ItemStack> def) {
        // Spigot doesn't have a decent implementation to load a list of these :shocker:
        // using config.set(path, List<ItemStack>) then loading this in and casting it to <ItemStack> has an awkwardly high memory usage...
        saveItemStackList(path, def); // add in our defaults

        if (config.getConfigurationSection(path) == null) //
            config.createSection(path);

        List<ItemStack> items = new ArrayList<>();
        for (String s : config.getConfigurationSection(path).getKeys(false)) {
            ItemStack itemStack = config.getItemStack(path + "." + s);
            items.add(itemStack);
        }
        return items;
    }

    public static void saveItemStackList(String path, List<ItemStack> def) {
        for (int i = 0; i < def.size(); i++) {
            config.addDefault(path + "." + i, def.get(i));
        }
    }

    public static void addItemStack(ItemStack def) {
        dragonDrops.add(def);
        saveItemStackList("drops", dragonDrops);
        saveConfig();
    }

    protected static void log(Level level, String s) {
        Bukkit.getLogger().log(level, s);
    }

    /** ONLY EDIT BELOW THIS LINE **/
    public static String enderDragonKilled = "<red>The ender dragon was killed by <players>.";
    public static String enderDragonSpawned = "<yellow>The ender dragon has been respawned.";
    public static long respawnDelay = 300;
    public static int expToSpread = 0;
    public static String worldName = "";
    private static void settings() {
        enderDragonKilled = getString("enderdragon.killed", enderDragonKilled);
        enderDragonSpawned = getString("enderdragon.spawned", enderDragonSpawned);
        respawnDelay = getLong("enderdragon.respawn-delay", respawnDelay);
        expToSpread = getInt("enderdragon.exp-to-drop", expToSpread);
        worldName = getString("worldname", worldName); // what world should we check for enderdragon fights, blanc default to the first world with dimension.end
    }

    public static int minX = -39; // distance from the end portal to the obsidian pillar
    public static int maxX = 39;
    public static int minZ = -39;
    public static int maxZ = 39;
    public static int minY = 70; // at what height should items drop
    public static int maxY = 80;
    public static int minPlayerCap = 1;
    public static int maxPlayerCap = 5;
    public static boolean clearDragonDrops = true; // remove any drops added by plugins/datapack
    private static void lootSettings() {
        clearDragonDrops = getBoolean("enderdragon.remove-drops", clearDragonDrops);
        minX = getInt("lootdrops.min-x", minX);
        maxX = getInt("lootdrops.max-x", maxX);
        minZ = getInt("lootdrops.min-z", minZ);
        maxZ = getInt("lootdrops.max-z", maxZ);
        minY = getInt("lootdrops.min-y", minY);
        maxY = getInt("lootdrops.max-y", maxY);
        minPlayerCap = getInt("lootdrops.min-player-count", minPlayerCap);
        maxPlayerCap = getInt("lootdrops.max-player-count", maxPlayerCap);
    }

    public static List<ItemStack> dragonDrops = List.of(new ItemStack(Material.DRAGON_EGG));
    private static void itemSettings() {
        dragonDrops = getItemStackList("drops", dragonDrops);
    }

}
