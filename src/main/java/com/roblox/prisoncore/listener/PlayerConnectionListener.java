package com.roblox.prisoncore.listener;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerConnectionListener implements Listener {
    private final PrisonCorePlugin plugin;

    public PlayerConnectionListener(PrisonCorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getDataManager().get(player);
        if (!hasPrisonPickaxe(player)) {
            player.getInventory().addItem(ItemUtil.createStarterPickaxe(plugin, player.getName()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getDataManager().unload(event.getPlayer());
    }

    private boolean hasPrisonPickaxe(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (ItemUtil.isPrisonPickaxe(plugin, item)) {
                return true;
            }
        }
        return false;
    }
}
