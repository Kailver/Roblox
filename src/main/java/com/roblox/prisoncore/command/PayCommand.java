package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand extends BaseCommand implements CommandExecutor {
    public PayCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ColorUtil.text("<red>Usage: /pay <player> <amount>"));
            return true;
        }
        Player player = (Player) sender;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(ColorUtil.text("<red>That player is not online."));
            return true;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException exception) {
            player.sendMessage(ColorUtil.text("<red>Amount must be numeric."));
            return true;
        }
        if (amount <= 0) {
            player.sendMessage(ColorUtil.text("<red>Amount must be greater than zero."));
            return true;
        }
        PlayerData payer = plugin.getDataManager().get(player);
        PlayerData receiver = plugin.getDataManager().get(target);
        if (!plugin.getEconomyManager().withdraw(payer, amount)) {
            player.sendMessage(ColorUtil.text("<red>You cannot afford that payment."));
            return true;
        }
        plugin.getEconomyManager().deposit(receiver, amount);
        player.sendMessage(ColorUtil.text("<green>You paid <white>" + target.getName() + " <green>" + plugin.getEconomyManager().format(amount)));
        target.sendMessage(ColorUtil.text("<green>You received <white>" + plugin.getEconomyManager().format(amount) + " <green>from <white>" + player.getName()));
        return true;
    }
}
