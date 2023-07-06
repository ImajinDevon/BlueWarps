package com.github.imajindevon.bluewarps.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class WarpConfig {
    private final Plugin plugin;
    private final int warpDelay;
    private final Map<String, Location> warps;

    public WarpConfig(
        @NotNull Plugin plugin,
        int warpDelay,
        @NotNull Map<String, Location> warps
    )
    {
        this.plugin = plugin;
        this.warpDelay = warpDelay;
        this.warps = warps;
    }

    public void copySaveAsync() {
        for (Map.Entry<String, Location> entry : this.warps.entrySet()) {
            this.plugin.getConfig().set(entry.getKey(), entry.getValue());
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this.plugin::saveConfig);
    }

    @Nullable
    public Location getWarpLocation(@NotNull String warpName) {
        return this.warps.get(warpName);
    }

    public void putWarp(@NotNull String warpName, @NotNull Location warpLocation) {
        this.warps.put(warpName, warpLocation);
    }

    public boolean removeWarp(@NotNull String warpName) {
        return this.warps.remove(warpName) != null;
    }

    public int warpDelay() {
        return this.warpDelay;
    }

    @NotNull
    public Set<String> warpNames() {
        return this.warps.keySet();
    }

    @Contract("_ -> new")
    public static WarpConfig loadOrCopyDefaults(@NotNull Plugin plugin)
    throws InvalidConfigurationException
    {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();

        int warpDelay = config.getInt("warp-delay");

        Set<String> warps = config.getKeys(false);
        Map<String, Location> warpMap = new HashMap<>(warps.size());

        for (String key : warps) {
            if (key.equals("warp-delay")) {
                continue;
            }
            Location location = config.getLocation(key);
            if (location == null) {
                throw new InvalidConfigurationException("Invalid location for warp '" + key + "'");
            }
            warpMap.put(key, location);
        }
        return new WarpConfig(plugin, warpDelay, warpMap);
    }
}
