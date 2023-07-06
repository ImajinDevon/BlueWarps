package com.github.imajindevon.bluewarps.config;

import com.github.imajindevon.bluelib.chat.ChatUtil;
import com.github.imajindevon.bluelib.chat.annotation.FutureColored;
import com.github.imajindevon.bluelib.config.reflection.ReflectiveConfig;
import com.github.imajindevon.bluelib.config.reflection.annotation.SuperPath;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MessagesConfig implements ReflectiveConfig {
    public String commandForPlayers = "(&6#&r) &6Only players can use this command!";

    @SuperPath("admin")
    public String configReloaded = "(&6*&r) &3Config reloaded!";

    public String initiateWarp = "(&e@&r) &bWarping in &3%warp-delay%&b seconds!";
    public String missingAdminPermission = "(&4!&r) &cYou don't have permission to do that!";
    public String missingSpecificWarpPermission = "(&4!&r) &cYou don't have permission to use this warp!";
    public String missingWarpsPermission = "(&4!&r) &cYou don't have permission to use warp commands!";
    public String notWarping = "(&e?&r) &eYou aren't currently warping!";
    public String prefix = "&bBlue&3Warps &3>&b> &r";
    public String specifyWarp = "(&7!&r) &3Please specify a warp!";
    public String unknownCommand = "(&7?&r) &7Unknown command!";
    public String unknownWarp = "(&cx&r) &cThat warp does not exist!";
    public String warpCancelled = "(&e/&r) &eWarp cancelled!";

    @SuperPath("admin")
    public String warpDeleted = "(&c-&r) &bWarp deleted.";

    @SuperPath("admin")
    public String warpSet = "(&a+&r) &bWarp set to this location.";

    @NotNull
    public String formatMessage(@NotNull @FutureColored String message) {
        return ChatUtil.translate(this.prefix + message);
    }

    public void ifAdminThen(@NotNull CommandSender sender, @NotNull Runnable runnable) {
        if (sender.hasPermission("bluewarps.admin")) {
            runnable.run();
        } else {
            sender.sendMessage(this.formatMessage(this.missingAdminPermission));
        }
    }
}
