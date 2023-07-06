package com.github.imajindevon.bluewarps;

import com.github.imajindevon.bluelib.config.reflection.ReflectivePluginConfig;
import com.github.imajindevon.bluewarps.config.MessagesConfig;
import com.github.imajindevon.bluewarps.config.WarpConfig;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public final class BlueWarpsPlugin extends JavaPlugin {
    private static BlueWarpsPlugin pluginInstance;
    private static WarpConfig warpConfig;

    @Override
    public void onDisable() {
        super.getLogger().severe("Plugin disabled.");
    }

    @Override
    public void onEnable() {
        pluginInstance = this;

        try {
            warpConfig = WarpConfig.loadOrCopyDefaults(this);
        } catch (InvalidConfigurationException exception) {
            exception.printStackTrace();
            this.logSevereAndDisable("Cannot load plugin because the config.yml configuration is invalid.");
            return;
        }

        ReflectivePluginConfig<MessagesConfig> messagesConfig;

        try {
            messagesConfig = ReflectivePluginConfig.loadOrCopyDefaultsAsync(
                this,
                "messages.yml",
                new MessagesConfig()
            );
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
            this.logSevereAndDisable(
                "Unable to start the plugin because an issue occurred while initializing configuration."
            );
            return;
        }

        PluginCommand command = super.getCommand("warp");

        if (command == null) {
            this.logSevereAndDisable("`warp` command does not exist, was it removed from the plugin.yml?");
            return;
        }
        CommandExecutor executor = new WarpTabExecutor(messagesConfig);
        command.setExecutor(executor);
    }

    private void logSevereAndDisable(@NotNull String @NotNull ... messages) {
        for (String message : messages) {
            super.getLogger().severe(message);
        }
        super.setEnabled(false);
    }

    public static void registerPlayerWarp(@NotNull UUID uuid, @NotNull Location location) {
        PlayerWarpHandler.registerPlayerWarpTask(pluginInstance, warpConfig.warpDelay(), location, uuid);
    }

    public static void reloadWarpConfig() throws InvalidConfigurationException {
        warpConfig = WarpConfig.loadOrCopyDefaults(pluginInstance);
    }

    @Nullable
    public static WarpConfig warpConfig() {
        return warpConfig;
    }
}
