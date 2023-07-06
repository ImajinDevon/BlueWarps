package com.github.imajindevon.bluewarps;

import com.github.imajindevon.bluelib.chat.ChatUtil;
import com.github.imajindevon.bluelib.config.reflection.ReflectivePluginConfig;
import com.github.imajindevon.bluewarps.config.MessagesConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarpCommand implements TabExecutor {
    private final ReflectivePluginConfig<MessagesConfig> messagesPluginConfig;

    WarpCommand(@NotNull ReflectivePluginConfig<MessagesConfig> messagesPluginConfig) {
        this.messagesPluginConfig = messagesPluginConfig;
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String @NotNull ... args
    )
    {
        if (!sender.hasPermission("bluewarps.warp")) {
            sender.sendMessage(ChatUtil.translate(this.messagesPluginConfig.config().missingWarpPermission));
            return true;
        }
        if (args.length == 0) {

        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String[] args
    )
    {
        return null;
    }
}
