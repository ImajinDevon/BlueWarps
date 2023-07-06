package com.github.imajindevon.bluewarps.config;

import com.github.imajindevon.bluelib.config.PathConfig;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class WarpConfigHandler {
    private final Map<String, Location> warps;
    private final PathConfig config;

    private WarpConfigHandler(@NotNull Map<String, Location> warps, @NotNull PathConfig config) {
        this.warps = warps;
        this.config = config;
    }

    public void setWarpLocation(@NotNull String warpName, @NotNull Location warpLocation) {
        this.warps.put(warpName, warpLocation);
    }

    @Nullable
    public Location getLocation(@NotNull String warpName) {
        return this.warps.get(warpName);
    }

    @NotNull
    public PathConfig config() {
        return this.config;
    }

    @Contract("_ -> new")
    public static WarpConfigHandler loadOrCopyDefaults(@NotNull PathConfig file)
    throws InvalidConfigurationException
    {
        int warpDelay = file.config().getInt("warp-delay");

        Set<String> warps = file.config().getKeys(false);
        Map<String, Location> warpMap = new HashMap<>(warps.size());

        for (String key : warps) {
            Location location = file.config().getLocation(key);
            if (location == null) {
                throw new InvalidConfigurationException("Invalid location for warp '" + key + "'");
            }
            warpMap.put(key, location);
        }
        return new WarpConfigHandler(warpMap, file);
    }
}
