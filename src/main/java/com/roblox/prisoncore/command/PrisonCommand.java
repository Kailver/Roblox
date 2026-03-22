package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrisonCommand extends BaseCommand implements CommandExecutor {
    public PrisonCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerData data = plugin.getDataManager().get(player);
        player.sendMessage(ColorUtil.text("<gold><bold>AI Prison Core</bold></gold>"));
        player.sendMessage(ColorUtil.text("<gray>Rank: <white>" + data.getRank() + " <gray>| Prestige: <white>" + data.getPrestige()));
        player.sendMessage(ColorUtil.text("<gray>Balance: <white>" + plugin.getEconomyManager().format(data.getBalance()) + " <gray>| Tokens: <white>" + data.getTokens()));
        player.sendMessage(ColorUtil.text("<gray>Auto Sell: <white>" + (data.isAutoSell() ? "Enabled" : "Disabled") + " <gray>| Backpack Value: <white>" + plugin.getEconomyManager().format(data.getBackpackValue())));
        player.sendMessage(ColorUtil.text("<gray>Core Commands: <white>/rankup, /prestige, /enchant, /pickaxe menu, /autosell, /backpack, /sellall"));
        return true;
    }
}
