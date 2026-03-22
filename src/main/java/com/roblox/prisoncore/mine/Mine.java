package com.roblox.prisoncore.mine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

public class Mine {
    private final String id;
    private final World world;
    private final Location pos1;
    private final Location pos2;
    private final int resetThreshold;
    private final Map<Material, Integer> composition;
    private final List<String> allowedRanks;

    public Mine(String id, World world, Location pos1, Location pos2, int resetThreshold, Map<Material, Integer> composition, List<String> allowedRanks) {
        this.id = id;
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.resetThreshold = resetThreshold;
        this.composition = composition;
        this.allowedRanks = allowedRanks;
    }

    public String getId() {
        return id;
    }

    public World getWorld() {
        return world;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public int getResetThreshold() {
        return resetThreshold;
    }

    public Map<Material, Integer> getComposition() {
        return composition;
    }

    public List<String> getAllowedRanks() {
        return allowedRanks;
    }

    public boolean contains(Block block) {
        if (block == null || block.getWorld() == null || !block.getWorld().equals(world)) {
            return false;
        }
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        Location location = block.getLocation();
        return location.getBlockX() >= minX && location.getBlockX() <= maxX
            && location.getBlockY() >= minY && location.getBlockY() <= maxY
            && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
    }

    public long getVolume() {
        return (long) (Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1)
            * (Math.abs(pos1.getBlockY() - pos2.getBlockY()) + 1)
            * (Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1);
    }

    public Material pickRandomMaterial() {
        int total = composition.values().stream().mapToInt(Integer::intValue).sum();
        int roll = ThreadLocalRandom.current().nextInt(Math.max(1, total));
        int current = 0;
        for (Map.Entry<Material, Integer> entry : composition.entrySet()) {
            current += entry.getValue();
            if (roll < current) {
                return entry.getKey();
            }
        }
        return Material.STONE;
    }

    public boolean isAccessibleBy(String rank) {
        return allowedRanks.isEmpty() || allowedRanks.stream().anyMatch(value -> value.equalsIgnoreCase(rank));
    }
}
