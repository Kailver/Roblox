package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.util.ColorUtil;
import com.roblox.prisoncore.util.ItemUtil;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PickaxeCommand extends BaseCommand implements CommandExecutor, TabCompleter {
    public PickaxeCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length > 0 && args[0].equalsIgnoreCase("give")) {
            if (!player.hasPermission("prisoncore.admin")) {
                player.sendMessage(ColorUtil.text("<red>No permission."));
                return true;
            }
            player.getInventory().addItem(ItemUtil.createStarterPickaxe(plugin, player.getName()));
            player.sendMessage(ColorUtil.text("<green>Added a new prison pickaxe to your inventory."));
            return true;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!ItemUtil.isPrisonPickaxe(plugin, hand)) {
            player.sendMessage(ColorUtil.text("<red>Hold your prison pickaxe or use /pickaxe give."));
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("menu")) {
            plugin.getEnchantMenu().open(player);
            return true;
        }
        ItemUtil.refreshPickaxe(plugin, hand);
        player.sendMessage(ColorUtil.text("<green>Your pickaxe lore has been refreshed."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("menu", "give");
        }
        return List.of();
    }
}
