package com.roblox.prisoncore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public final class LocationUtil {
    private LocationUtil() {
    }

    public static Location fromSection(World world, ConfigurationSection section) {
        if (world == null || section == null) {
            return null;
        }
        return new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"));
    }

    public static void save(Location location, ConfigurationSection section) {
        section.set("x", location.getBlockX());
        section.set("y", location.getBlockY());
        section.set("z", location.getBlockZ());
    }

    public static World world(String name) {
        return name == null ? null : Bukkit.getWorld(name);
    }
}
