package com.archyx.statisticapi;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

public class VersionUtil {

    private static final int version = Integer.parseInt(getMajorVersion(Bukkit.getVersion()).substring(2));

    public static int getVersion() {
        return version;
    }

    private static String getMajorVersion(String version) {
        Validate.notEmpty(version, "Cannot get major Minecraft version from null or empty string");

        // getVersion()
        int index = version.lastIndexOf("MC:");
        if (index != -1) {
            version = version.substring(index + 4, version.length() - 1);
        } else if (version.endsWith("SNAPSHOT")) {
            // getBukkitVersion()
            index = version.indexOf('-');
            version = version.substring(0, index);
        }

        // 1.13.2, 1.14.4, etc...
        int lastDot = version.lastIndexOf('.');
        if (version.indexOf('.') != lastDot) version = version.substring(0, lastDot);

        return version;
    }

}
