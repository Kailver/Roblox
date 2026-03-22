package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackpackCommand extends BaseCommand implements CommandExecutor {
    public BackpackCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerData data = plugin.getDataManager().get(player);
        if (args.length > 0 && args[0].equalsIgnoreCase("sell")) {
            if (data.getBackpackValue() <= 0) {
                player.sendMessage(ColorUtil.text("<yellow>Your backpack is empty."));
                return true;
            }
            plugin.getEconomyManager().deposit(data, data.getBackpackValue());
            player.sendMessage(ColorUtil.text("<green>Sold your backpack for <white>" + plugin.getEconomyManager().format(data.getBackpackValue())));
            data.clearBackpack();
            return true;
        }
        player.sendMessage(ColorUtil.text("<gold>Backpack Blocks: <white>" + data.getBackpackBlocks()));
        player.sendMessage(ColorUtil.text("<gold>Backpack Value: <white>" + plugin.getEconomyManager().format(data.getBackpackValue())));
        player.sendMessage(ColorUtil.text("<yellow>Use /backpack sell to cash out stored blocks."));
        return true;
    }
}
