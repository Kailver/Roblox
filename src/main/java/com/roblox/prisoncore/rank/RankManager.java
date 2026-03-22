package com.roblox.prisoncore.rank;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.configuration.ConfigurationSection;

public class RankManager {
    private final PrisonCorePlugin plugin;
    private final List<String> ranks = new ArrayList<>();

    public RankManager(PrisonCorePlugin plugin) {
        this.plugin = plugin;
        ConfigurationSection costs = plugin.getConfig().getConfigurationSection("rank-costs");
        if (costs != null) {
            for (String key : costs.getKeys(false)) {
                ranks.add(key.toUpperCase(Locale.ROOT));
            }
            Collections.sort(ranks);
        }
    }

    public List<String> getRanks() {
        return ranks;
    }

    public String getNextRank(String rank) {
        int index = ranks.indexOf(rank.toUpperCase(Locale.ROOT));
        if (index < 0 || index + 1 >= ranks.size()) {
            return null;
        }
        return ranks.get(index + 1);
    }

    public double getRankCost(String rank) {
        return plugin.getConfig().getDouble("rank-costs." + rank.toUpperCase(Locale.ROOT), Double.MAX_VALUE);
    }

    public double getPrestigeCost(int prestigeLevel) {
        return plugin.getConfig().getDouble("prestige-costs." + prestigeLevel, Double.MAX_VALUE);
    }

    public boolean canRankUp(PlayerData data) {
        String next = getNextRank(data.getRank());
        return next != null && data.getBalance() >= getRankCost(next);
    }

    public boolean rankUp(PlayerData data) {
        String next = getNextRank(data.getRank());
        if (next == null) {
            return false;
        }
        double cost = getRankCost(next);
        if (data.getBalance() < cost) {
            return false;
        }
        data.setBalance(data.getBalance() - cost);
        data.setRank(next);
        return true;
    }

    public boolean canPrestige(PlayerData data) {
        int nextPrestige = data.getPrestige() + 1;
        return "Z".equalsIgnoreCase(data.getRank())
            && nextPrestige <= plugin.getConfig().getInt("max-prestige", 10)
            && data.getBalance() >= getPrestigeCost(nextPrestige);
    }

    public boolean prestige(PlayerData data) {
        if (!canPrestige(data)) {
            return false;
        }
        int nextPrestige = data.getPrestige() + 1;
        data.setBalance(data.getBalance() - getPrestigeCost(nextPrestige));
        data.setPrestige(nextPrestige);
        data.setRank(plugin.getConfig().getString("starting-rank", "A"));
        return true;
    }
}
