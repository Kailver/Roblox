package com.roblox.prisoncore.listener;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.enchant.CustomEnchant;
import com.roblox.prisoncore.menu.EnchantMenuHolder;
import com.roblox.prisoncore.util.ColorUtil;
import com.roblox.prisoncore.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class EnchantMenuListener implements Listener {
    private final PrisonCorePlugin plugin;

    public EnchantMenuListener(PrisonCorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnchantMenuHolder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player) || event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }
        String enchantKey = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ItemUtil.key(plugin, "menu_enchant"), PersistentDataType.STRING);
        if (enchantKey == null) {
            return;
        }
        ItemStack pickaxe = player.getInventory().getItemInMainHand();
        if (!ItemUtil.isPrisonPickaxe(plugin, pickaxe)) {
            player.closeInventory();
            player.sendMessage(ColorUtil.text("<red>You must keep holding your prison pickaxe while upgrading enchants."));
            return;
        }
        CustomEnchant enchant = CustomEnchant.fromInput(enchantKey);
        if (enchant == null) {
            return;
        }
        int amount = event.isShiftClick() ? 10 : (event.isRightClick() ? 5 : 1);
        PlayerData data = plugin.getDataManager().get(player);
        long cost = plugin.getEnchantManager().getUpgradeCost(pickaxe, enchant, Math.min(amount, enchant.getMaxLevel() - ItemUtil.getEnchantLevel(plugin, pickaxe, enchant)));
        if (!plugin.getEnchantManager().upgrade(data, pickaxe, enchant, amount)) {
            player.sendMessage(ColorUtil.text("<red>Unable to upgrade <white>" + enchant.getDisplayName() + "<red>. Cost: <white>" + cost + " tokens"));
            return;
        }
        player.sendMessage(ColorUtil.text("<green>Upgraded <white>" + enchant.getDisplayName() + " <green>to <white>" + ItemUtil.getEnchantLevel(plugin, pickaxe, enchant)));
        plugin.getEnchantMenu().open(player);
    }
}
