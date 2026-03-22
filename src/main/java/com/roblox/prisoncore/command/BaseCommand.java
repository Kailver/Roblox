package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand {
    protected final PrisonCorePlugin plugin;

    protected BaseCommand(PrisonCorePlugin plugin) {
        this.plugin = plugin;
    }

    protected boolean requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.text("<red>Only players can use this command."));
            return false;
        }
        return true;
    }
}
