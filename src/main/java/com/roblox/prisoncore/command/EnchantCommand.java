package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.enchant.CustomEnchant;
import com.roblox.prisoncore.util.ColorUtil;
import com.roblox.prisoncore.util.ItemUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand extends BaseCommand implements CommandExecutor, TabCompleter {
    public EnchantCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        Player player = (Player) sender;
        ItemStack pickaxe = player.getInventory().getItemInMainHand();
        if (!ItemUtil.isPrisonPickaxe(plugin, pickaxe)) {
            player.sendMessage(ColorUtil.text("<red>Hold your prison pickaxe first."));
            return true;
        }
        if (args.length == 0) {
            plugin.getEnchantMenu().open(player);
            return true;
        }

        CustomEnchant enchant = CustomEnchant.fromInput(args[0]);
        if (enchant == null) {
            player.sendMessage(ColorUtil.text("<red>Unknown enchant."));
            return true;
        }
        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException exception) {
                player.sendMessage(ColorUtil.text("<red>Levels must be numeric."));
                return true;
            }
        }
        PlayerData data = plugin.getDataManager().get(player);
        long cost = plugin.getEnchantManager().getUpgradeCost(pickaxe, enchant, Math.min(amount, enchant.getMaxLevel() - ItemUtil.getEnchantLevel(plugin, pickaxe, enchant)));
        if (!plugin.getEnchantManager().upgrade(data, pickaxe, enchant, amount)) {
            player.sendMessage(ColorUtil.text("<red>Could not upgrade that enchant. Cost: <white>" + cost + " tokens"));
            return true;
        }
        player.sendMessage(ColorUtil.text("<green>Upgraded <white>" + enchant.getDisplayName() + " <green>to level <white>" + ItemUtil.getEnchantLevel(plugin, pickaxe, enchant)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(CustomEnchant.values()).map(CustomEnchant::getKey).filter(value -> value.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return List.of();
    }
}
