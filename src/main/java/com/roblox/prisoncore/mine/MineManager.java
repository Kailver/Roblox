package com.roblox.prisoncore.mine;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.util.LocationUtil;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockState;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

public class MineManager {
    private final PrisonCorePlugin plugin;
    private final File file;
    private final YamlConfiguration config;
    private final Map<String, Mine> mines = new HashMap<>();
    private BukkitTask resetTask;

    public MineManager(PrisonCorePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "mines.yml");
        if (!file.exists()) {
            plugin.saveResource("mines.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
        startResetLoop();
    }

    public void shutdown() {
        if (resetTask != null) {
            resetTask.cancel();
        }
    }

    public void load() {
        mines.clear();
        ConfigurationSection minesSection = config.getConfigurationSection("mines");
        if (minesSection == null) {
            return;
        }
        for (String id : minesSection.getKeys(false)) {
            ConfigurationSection section = minesSection.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            World world = LocationUtil.world(section.getString("world"));
            Location pos1 = LocationUtil.fromSection(world, section.getConfigurationSection("pos1"));
            Location pos2 = LocationUtil.fromSection(world, section.getConfigurationSection("pos2"));
            if (world == null || pos1 == null || pos2 == null) {
                continue;
            }
            Map<Material, Integer> composition = new EnumMap<>(Material.class);
            ConfigurationSection compositionSection = section.getConfigurationSection("composition");
            if (compositionSection != null) {
                for (String materialName : compositionSection.getKeys(false)) {
                    Material material = Material.matchMaterial(materialName);
                    if (material != null) {
                        composition.put(material, compositionSection.getInt(materialName));
                    }
                }
            }
            List<String> allowedRanks = new ArrayList<>(section.getStringList("allowed-ranks"));
            mines.put(id.toLowerCase(Locale.ROOT), new Mine(
                id,
                world,
                pos1,
                pos2,
                section.getInt("reset-threshold", 35),
                composition,
                allowedRanks
            ));
        }
    }

    public Optional<Mine> getMine(String id) {
        return Optional.ofNullable(mines.get(id.toLowerCase(Locale.ROOT)));
    }

    public Optional<Mine> getMine(Block block) {
        return mines.values().stream().filter(mine -> mine.contains(block)).findFirst();
    }

    public List<Mine> getMines() {
        return new ArrayList<>(mines.values());
    }

    public void resetMine(Mine mine) {
        CuboidRegion region = new CuboidRegion(
            BlockVector3.at(mine.getPos1().getBlockX(), mine.getPos1().getBlockY(), mine.getPos1().getBlockZ()),
            BlockVector3.at(mine.getPos2().getBlockX(), mine.getPos2().getBlockY(), mine.getPos2().getBlockZ())
        );

        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(mine.getWorld()))) {
            for (BlockVector3 vector : region) {
                Material material = mine.pickRandomMaterial();
                BlockState state = BukkitAdapter.asBlockType(material).getDefaultState();
                session.setBlock(vector, state);
            }
            Operations.complete(session.commit());
        } catch (Exception exception) {
            plugin.getLogger().warning("Failed to reset mine " + mine.getId() + " via FAWE/WorldEdit: " + exception.getMessage());
        }
    }

    public void createMine(String id, Location pos1, Location pos2, int resetThreshold, Map<Material, Integer> composition, List<String> allowedRanks) {
        ConfigurationSection section = config.createSection("mines." + id);
        section.set("world", pos1.getWorld().getName());
        LocationUtil.save(pos1, section.createSection("pos1"));
        LocationUtil.save(pos2, section.createSection("pos2"));
        section.set("reset-threshold", resetThreshold);
        ConfigurationSection compositionSection = section.createSection("composition");
        composition.forEach((material, weight) -> compositionSection.set(material.name(), weight));
        section.set("allowed-ranks", allowedRanks);
        saveFile();
        load();
    }

    private void saveFile() {
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning("Unable to save mines.yml: " + exception.getMessage());
        }
    }

    private void startResetLoop() {
        long interval = plugin.getConfig().getLong("mine-reset-check-interval-seconds", 15L) * 20L;
        resetTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Mine mine : mines.values()) {
                if (remainingPercentage(mine) <= mine.getResetThreshold()) {
                    resetMine(mine);
                }
            }
        }, interval, interval);
    }

    public int remainingPercentage(Mine mine) {
        long solid = 0;
        int minX = Math.min(mine.getPos1().getBlockX(), mine.getPos2().getBlockX());
        int minY = Math.min(mine.getPos1().getBlockY(), mine.getPos2().getBlockY());
        int minZ = Math.min(mine.getPos1().getBlockZ(), mine.getPos2().getBlockZ());
        int maxX = Math.max(mine.getPos1().getBlockX(), mine.getPos2().getBlockX());
        int maxY = Math.max(mine.getPos1().getBlockY(), mine.getPos2().getBlockY());
        int maxZ = Math.max(mine.getPos1().getBlockZ(), mine.getPos2().getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (!mine.getWorld().getBlockAt(x, y, z).getType().isAir()) {
                        solid++;
                    }
                }
            }
        }
        return (int) Math.round((solid * 100.0) / mine.getVolume());
    }
}
