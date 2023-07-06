package com.github.imajindevon.bluewarps;

import com.github.imajindevon.bluelib.config.reflection.ReflectiveConfig;
import com.github.imajindevon.bluelib.config.reflection.annotation.SuperPath;

public class BlueWarpsMessages implements ReflectiveConfig {
    public String prefix = "&3Blue&bWarps &3>&b> &r";
    public String initiateWarp = "&bWarping in &3%remaining%&b seconds!";
    public String insufficientPermission = "&cYou don't have enough permission to use this warp!";

    @SuperPath("admin")
    public String warpCreated = "[&a+&r] &bWarp created.";

    @SuperPath("admin")
    public String warpDeleted = "[&c-&r] &bWarp deleted.";
}
