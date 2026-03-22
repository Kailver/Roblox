package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand extends BaseCommand implements CommandExecutor {
    public BalanceCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerData data = plugin.getDataManager().get(player);
        player.sendMessage(ColorUtil.text("<gold>Balance: <white>" + plugin.getEconomyManager().format(data.getBalance())));
        return true;
    }
}
