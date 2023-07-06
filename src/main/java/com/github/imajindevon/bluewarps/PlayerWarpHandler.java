package com.github.imajindevon.bluewarps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerWarpHandler {
    private static final Map<UUID, BukkitTask> PLAYER_WARP_MAP = new HashMap<>();

    private PlayerWarpHandler() {
    }

    public static boolean cancelPlayerWarpTask(@NotNull UUID uuid) {
        BukkitTask warpTask = PLAYER_WARP_MAP.remove(uuid);
        if (warpTask == null) {
            return false;
        }
        Bukkit.getScheduler().cancelTask(warpTask.getTaskId());
        return true;
    }

    public static void registerPlayerWarpTask(
        @NotNull Plugin plugin,
        int waitDelaySec,
        @NotNull Location location,
        @NotNull UUID uuid
    )
    {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
            }
            PLAYER_WARP_MAP.remove(player.getUniqueId());
        }, waitDelaySec * 20L);
        PLAYER_WARP_MAP.put(uuid, bukkitTask);
    }
}
