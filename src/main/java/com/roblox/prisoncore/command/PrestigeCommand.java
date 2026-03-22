package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrestigeCommand extends BaseCommand implements CommandExecutor {
    public PrestigeCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerData data = plugin.getDataManager().get(player);
        int nextPrestige = data.getPrestige() + 1;
        double cost = plugin.getRankManager().getPrestigeCost(nextPrestige);
        if (!plugin.getRankManager().prestige(data)) {
            player.sendMessage(ColorUtil.text("<red>You must reach rank Z and have <white>" + plugin.getEconomyManager().format(cost) + " <red>to prestige."));
            return true;
        }
        player.sendMessage(ColorUtil.text("<gold>Congratulations! You are now Prestige <white>" + data.getPrestige()));
        return true;
    }
}
