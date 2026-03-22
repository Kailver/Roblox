package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankupCommand extends BaseCommand implements CommandExecutor {
    public RankupCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerData data = plugin.getDataManager().get(player);
        String nextRank = plugin.getRankManager().getNextRank(data.getRank());
        if (nextRank == null) {
            player.sendMessage(ColorUtil.text("<yellow>You are already at the max rank. Try /prestige."));
            return true;
        }
        double cost = plugin.getRankManager().getRankCost(nextRank);
        if (!plugin.getRankManager().rankUp(data)) {
            player.sendMessage(ColorUtil.text("<red>You need <white>" + plugin.getEconomyManager().format(cost) + " <red>to rank up to <white>" + nextRank));
            return true;
        }
        player.sendMessage(ColorUtil.text("<green>Ranked up to <white>" + data.getRank() + " <green>for <white>" + plugin.getEconomyManager().format(cost)));
        return true;
    }
}
