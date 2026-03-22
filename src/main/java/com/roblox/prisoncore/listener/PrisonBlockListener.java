package com.roblox.prisoncore.listener;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.mine.Mine;
import com.roblox.prisoncore.util.ColorUtil;
import com.roblox.prisoncore.util.ItemUtil;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PrisonBlockListener implements Listener {
    private final PrisonCorePlugin plugin;

    public PrisonBlockListener(PrisonCorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = player.getInventory().getItemInMainHand();
        if (!ItemUtil.isPrisonPickaxe(plugin, pickaxe)) {
            return;
        }

        Block block = event.getBlock();
        Mine mine = plugin.getMineManager().getMine(block).orElse(null);
        if (mine == null) {
            return;
        }
        PlayerData data = plugin.getDataManager().get(player);
        if (!mine.isAccessibleBy(data.getRank())) {
            event.setCancelled(true);
            player.sendMessage(ColorUtil.text("<red>Your current rank cannot mine here."));
            return;
        }

        event.setDropItems(false);
        event.setExpToDrop(0);
        plugin.getEnchantManager().applyPlayerBuffs(player, pickaxe);

        Set<Block> blocksToProcess = new HashSet<>();
        blocksToProcess.add(block);
        blocksToProcess.addAll(plugin.getEnchantManager().collectExtraBlocks(pickaxe, block));

        int broken = 0;
        double totalValue = 0D;
        long totalTokens = 0L;
        int totalExperience = 0;

        for (Block target : blocksToProcess) {
            if (target == null || target.getType().isAir() || target.getType() == Material.BEDROCK || !mine.contains(target)) {
                continue;
            }
            double baseValue = plugin.getConfig().getDouble("block-values." + target.getType().name(), 0D);
            long baseTokens = plugin.getConfig().getLong("token-values." + target.getType().name(), 0L);
            totalValue += plugin.getEnchantManager().applyValueModifiers(pickaxe, target, baseValue);
            totalTokens += plugin.getEnchantManager().applyTokenModifiers(pickaxe, target, baseTokens);
            totalExperience += plugin.getEnchantManager().bonusExperience(pickaxe);
            target.setType(Material.AIR, false);
            broken++;
        }

        if (broken <= 0) {
            return;
        }

        data.setTokens(data.getTokens() + totalTokens);
        if (data.isAutoSell()) {
            plugin.getEconomyManager().deposit(data, totalValue);
        } else {
            data.addBackpackBlocks(broken);
            data.addBackpackValue(totalValue);
        }
        if (totalExperience > 0) {
            player.giveExp(totalExperience);
        }
        ItemUtil.addBlocksMined(plugin, pickaxe, broken);
        ItemUtil.addValueSold(plugin, pickaxe, totalValue);
        ItemUtil.refreshPickaxe(plugin, pickaxe);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.getMineManager().getMine(event.getBlock()).isPresent()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ColorUtil.text("<red>You cannot place blocks inside prison mines."));
        }
    }
}
