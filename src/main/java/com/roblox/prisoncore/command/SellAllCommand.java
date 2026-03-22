package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellAllCommand extends BaseCommand implements CommandExecutor {
    public SellAllCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerData data = plugin.getDataManager().get(player);
        double total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            double value = plugin.getConfig().getDouble("block-values." + item.getType().name(), 0);
            if (value <= 0) {
                continue;
            }
            total += value * item.getAmount();
            item.setAmount(0);
        }
        if (total <= 0) {
            player.sendMessage(ColorUtil.text("<yellow>You have nothing sellable in your inventory."));
            return true;
        }
        plugin.getEconomyManager().deposit(data, total);
        player.sendMessage(ColorUtil.text("<green>Sold all blocks for <white>" + plugin.getEconomyManager().format(total)));
        return true;
    }
}
