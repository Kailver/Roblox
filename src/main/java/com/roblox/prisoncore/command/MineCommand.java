package com.roblox.prisoncore.command;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.mine.Mine;
import com.roblox.prisoncore.util.ColorUtil;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class MineCommand extends BaseCommand implements CommandExecutor, TabCompleter {
    private final Map<String, Location> pos1Selections = new HashMap<>();
    private final Map<String, Location> pos2Selections = new HashMap<>();

    public MineCommand(PrisonCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!requirePlayer(sender)) {
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("prisoncore.admin")) {
            player.sendMessage(ColorUtil.text("<red>No permission."));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(ColorUtil.text("<yellow>/mine pos1, /mine pos2, /mine create <id> <threshold> <MATERIAL:WEIGHT,...> [ranks], /mine reset <id>, /mine info <id>, /mine list"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "pos1" -> {
                pos1Selections.put(player.getName(), player.getLocation());
                player.sendMessage(ColorUtil.text("<green>Saved position 1 at your location."));
            }
            case "pos2" -> {
                pos2Selections.put(player.getName(), player.getLocation());
                player.sendMessage(ColorUtil.text("<green>Saved position 2 at your location."));
            }
            case "create" -> createMine(player, args);
            case "reset" -> resetMine(player, args);
            case "info" -> infoMine(player, args);
            case "list" -> player.sendMessage(ColorUtil.text("<aqua>Mines: <white>" + plugin.getMineManager().getMines().stream().map(Mine::getId).collect(Collectors.joining(", "))));
            default -> player.sendMessage(ColorUtil.text("<red>Unknown subcommand."));
        }
        return true;
    }

    private void createMine(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(ColorUtil.text("<red>Usage: /mine create <id> <threshold> <MATERIAL:WEIGHT,...> [ranks]"));
            return;
        }
        Location pos1 = pos1Selections.get(player.getName());
        Location pos2 = pos2Selections.get(player.getName());
        if (pos1 == null || pos2 == null) {
            player.sendMessage(ColorUtil.text("<red>Select pos1 and pos2 first."));
            return;
        }
        int threshold;
        try {
            threshold = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            player.sendMessage(ColorUtil.text("<red>Threshold must be numeric."));
            return;
        }
        Map<Material, Integer> composition = new EnumMap<>(Material.class);
        for (String entry : args[3].split(",")) {
            String[] split = entry.split(":");
            if (split.length != 2) {
                continue;
            }
            Material material = Material.matchMaterial(split[0]);
            if (material == null) {
                continue;
            }
            composition.put(material, Integer.parseInt(split[1]));
        }
        List<String> ranks = args.length >= 5 ? List.of(args[4].split(",")) : List.of();
        plugin.getMineManager().createMine(args[1], pos1, pos2, threshold, composition, ranks);
        player.sendMessage(ColorUtil.text("<green>Created mine <white>" + args[1]));
    }

    private void resetMine(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.text("<red>Usage: /mine reset <id>"));
            return;
        }
        plugin.getMineManager().getMine(args[1]).ifPresentOrElse(mine -> {
            plugin.getMineManager().resetMine(mine);
            player.sendMessage(ColorUtil.text("<green>Reset mine <white>" + mine.getId()));
        }, () -> player.sendMessage(ColorUtil.text("<red>Unknown mine.")));
    }

    private void infoMine(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ColorUtil.text("<red>Usage: /mine info <id>"));
            return;
        }
        plugin.getMineManager().getMine(args[1]).ifPresentOrElse(mine -> {
            player.sendMessage(ColorUtil.text("<gold>Mine <white>" + mine.getId()));
            player.sendMessage(ColorUtil.text("<gray>Remaining: <white>" + plugin.getMineManager().remainingPercentage(mine) + "%"));
            player.sendMessage(ColorUtil.text("<gray>Allowed ranks: <white>" + String.join(", ", mine.getAllowedRanks())));
        }, () -> player.sendMessage(ColorUtil.text("<red>Unknown mine.")));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("pos1", "pos2", "create", "reset", "info", "list");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("info"))) {
            return plugin.getMineManager().getMines().stream().map(Mine::getId).collect(Collectors.toList());
        }
        return List.of();
    }
}
