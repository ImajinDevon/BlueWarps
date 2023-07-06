package com.github.imajindevon.bluewarps;

import com.github.imajindevon.bluelib.config.reflection.ReflectivePluginConfig;
import com.github.imajindevon.bluelib.util.collection.CollectionUtil;
import com.github.imajindevon.bluewarps.config.MessagesConfig;
import com.github.imajindevon.bluewarps.config.WarpConfig;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

public class WarpTabExecutor implements TabExecutor {
    private static final Map<String, String> ADMIN_SUBCOMMAND_DESCRIPTIONS = Map.of(
        "delete", "Delete a warp.",
        "reload_messages", "Loads the current messages configuration. May lag the server.",
        "reload_warps", "Discards warps in memory and loads the warp configuration from config.yml.",
        "set", "Set the given warp's location.",
        "help", "Get help regarding BlueWarps' commands."
    );

    private static final List<String>
        ADMIN_TAB_COMPLETIONS =
        List.of("delete", "reload_messages", "reload_warps", "set", "help");

    private static final List<String> TAB_COMPLETIONS = List.of("cancel");

    private final MessagesConfig config;
    private final ReflectivePluginConfig<MessagesConfig> configFile;

    WarpTabExecutor(@NotNull ReflectivePluginConfig<MessagesConfig> configFile) {
        this.configFile = configFile;
        this.config = configFile.config();
    }

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String @NotNull ... args
    )
    {
        boolean notAdmin = !sender.hasPermission("bluewarps.admin");

        if (!sender.hasPermission("bluewarps.warp")) {
            sender.sendMessage(this.config.formatMessage(this.config.missingWarpsPermission));
            return true;
        } else if (args.length == 0) {
            sender.sendMessage(this.config.formatMessage(this.config.specifyWarp));
            return true;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);

        if (args.length == 1) {
            switch (subcommand) {
                case "help" -> this.config.ifAdminThen(sender, () -> {
                    for (Map.Entry<String, String> entry : ADMIN_SUBCOMMAND_DESCRIPTIONS.entrySet()) {
                        sender.sendMessage(this.config.formatMessage("&3/warp "
                            + entry.getKey()
                            + " | &b"
                            + entry.getValue()));
                    }
                });
                case "reload_messages" -> this.config.ifAdminThen(sender, () -> {
                    this.configFile.tryReloadFromFile();
                    sender.sendMessage(this.config.formatMessage(this.config.configReloaded));
                });
                case "reload_warps" -> this.config.ifAdminThen(sender, () -> {
                    try {
                        BlueWarpsPlugin.reloadWarpConfig();
                        sender.sendMessage(this.config.formatMessage(this.config.configReloaded));
                    } catch (InvalidConfigurationException exception) {
                        exception.printStackTrace();
                        sender.sendMessage(this.config.formatMessage(
                            "[&c!&r]&c&l An exception occurred, check console."
                        ));
                    }
                });
                case "cancel" -> sender.sendMessage(this.config.formatMessage(
                    sender instanceof Player player
                        ? (
                        PlayerWarpHandler.cancelPlayerWarpTask(player.getUniqueId())
                            ? this.config.warpCancelled
                            : this.config.notWarping
                    )
                        : this.config.commandForPlayers
                ));
                default -> {
                    WarpConfig warpConfig = BlueWarpsPlugin.warpConfig();
                    Location warpLocation = warpConfig.getWarpLocation(subcommand);

                    if (warpLocation == null) {
                        sender.sendMessage(this.config.formatMessage(this.config.unknownWarp));
                    } else if (!(sender instanceof Player player)) {
                        sender.sendMessage(this.config.formatMessage(this.config.commandForPlayers));
                    } else if (player.hasPermission("bluewarps.warp." + subcommand)) {
                        BlueWarpsPlugin.registerPlayerWarp(player.getUniqueId(), warpLocation);

                        player.sendMessage(this.config.formatMessage(this.config.initiateWarp).replace(
                            "%warp-delay%",
                            String.valueOf(warpConfig.warpDelay())
                        ));
                    } else {
                        player.sendMessage(this.config.formatMessage(this.config.missingSpecificWarpPermission));
                    }
                }
            }
        } else if (args.length == 2) {
            if (subcommand.equalsIgnoreCase("set")) {
                if (notAdmin) {
                    sender.sendMessage(this.config.formatMessage(this.config.missingAdminPermission));
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(this.config.formatMessage(this.config.commandForPlayers));
                    return true;
                }
                WarpConfig config = BlueWarpsPlugin.warpConfig();
                config.putWarp(args[1], player.getLocation());
                config.copySaveAsync();
                sender.sendMessage(this.config.formatMessage(this.config.warpSet));
            } else if (subcommand.equalsIgnoreCase("delete")) {
                this.config.ifAdminThen(
                    sender,
                    () -> sender.sendMessage(this.config.formatMessage(BlueWarpsPlugin
                        .warpConfig()
                        .removeWarp(args[1])
                        ?
                        this.config.warpDeleted
                        : this.config.unknownWarp))
                );
            } else {
                sender.sendMessage(this.config.formatMessage(this.config.unknownCommand));
            }
        } else {
            sender.sendMessage(this.config.formatMessage(this.config.unknownCommand));
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
        if (args.length > 2) {
            return null;
        } else if (args.length == 0) {
            List<String> completions =
                new ArrayList<>(filterWarpNames(s -> sender.hasPermission("bluewarps.warp." + s)));

            completions.addAll(TAB_COMPLETIONS);

            if (sender.hasPermission("bluewarps.admin")) {
                System.out.println("admin");
                completions.addAll(ADMIN_TAB_COMPLETIONS);
            }
            return completions;
        }

        String arg = args[0];

        List<String> completions =
            new ArrayList<>(filterWarpNames(s -> s.startsWith(arg) && sender.hasPermission("bluewarps.warp." + s)));

        if (args.length == 1) {
            completions.addAll(CollectionUtil.filterStartsWith(arg, TAB_COMPLETIONS));
            if (sender.hasPermission("bluewarps.admin")) {
                completions.addAll(CollectionUtil.filterStartsWith(arg, ADMIN_TAB_COMPLETIONS));
            }
            return completions;
        }
        if (!arg.equalsIgnoreCase("delete")) {
            return null;
        }
        String warpArg = args[1];
        return filterWarpNames(s -> s.startsWith(warpArg));
    }

    @NotNull
    private static List<String> filterWarpNames(@NotNull Predicate<String> predicate) {
        return BlueWarpsPlugin.warpConfig().warpNames().stream().filter(predicate).toList();
    }
}
