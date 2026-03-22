package com.roblox.prisoncore.data;

import com.roblox.prisoncore.PrisonCorePlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class DataManager {
    private final PrisonCorePlugin plugin;
    private final File playerDirectory;
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public DataManager(PrisonCorePlugin plugin) {
        this.plugin = plugin;
        this.playerDirectory = new File(plugin.getDataFolder(), "players");
        if (!playerDirectory.exists()) {
            playerDirectory.mkdirs();
        }
    }

    public PlayerData get(Player player) {
        return cache.computeIfAbsent(player.getUniqueId(), id -> load(id, player.getName()));
    }

    public void saveAll() {
        cache.values().forEach(this::save);
    }

    public void unload(Player player) {
        PlayerData data = cache.remove(player.getUniqueId());
        if (data != null) {
            save(data);
        }
    }

    private PlayerData load(UUID uniqueId, String lastKnownName) {
        File file = file(uniqueId);
        PlayerData data = new PlayerData(uniqueId);
        data.setBalance(plugin.getConfig().getDouble("starting-balance"));
        data.setTokens(plugin.getConfig().getLong("starting-tokens"));
        data.setRank(plugin.getConfig().getString("starting-rank", "A"));
        data.setPrestige(0);
        data.setAutoSell(plugin.getConfig().getBoolean("starting-auto-sell", true));

        if (!file.exists()) {
            return data;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        data.setBalance(config.getDouble("balance", data.getBalance()));
        data.setTokens(config.getLong("tokens", data.getTokens()));
        data.setRank(config.getString("rank", data.getRank()));
        data.setPrestige(config.getInt("prestige", 0));
        data.setAutoSell(config.getBoolean("auto-sell", data.isAutoSell()));
        data.addBackpackBlocks(config.getLong("backpack.blocks", 0));
        data.addBackpackValue(config.getDouble("backpack.value", 0D));
        config.set("last-name", lastKnownName);
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to update player file for " + uniqueId + ": " + exception.getMessage());
        }
        return data;
    }

    public void save(PlayerData data) {
        File file = file(data.getUniqueId());
        YamlConfiguration config = new YamlConfiguration();
        config.set("balance", data.getBalance());
        config.set("tokens", data.getTokens());
        config.set("rank", data.getRank());
        config.set("prestige", data.getPrestige());
        config.set("auto-sell", data.isAutoSell());
        config.set("backpack.blocks", data.getBackpackBlocks());
        config.set("backpack.value", data.getBackpackValue());
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save player file for " + data.getUniqueId() + ": " + exception.getMessage());
        }
    }

    private File file(UUID uniqueId) {
        return new File(playerDirectory, uniqueId + ".yml");
    }
}
