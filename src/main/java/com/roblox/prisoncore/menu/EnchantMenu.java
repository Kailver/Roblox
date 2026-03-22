package com.roblox.prisoncore.menu;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.enchant.CustomEnchant;
import com.roblox.prisoncore.util.ColorUtil;
import com.roblox.prisoncore.util.ItemUtil;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EnchantMenu {
    private static final List<Integer> ENCHANT_SLOTS = List.of(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42
    );

    private final PrisonCorePlugin plugin;

    public EnchantMenu(PrisonCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        ItemStack pickaxe = player.getInventory().getItemInMainHand();
        if (!ItemUtil.isPrisonPickaxe(plugin, pickaxe)) {
            player.sendMessage(ColorUtil.text("<red>Hold your prison pickaxe to use the enchant menu."));
            return;
        }
        PlayerData data = plugin.getDataManager().get(player);
        EnchantMenuHolder holder = new EnchantMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, 54, "Prison Enchants");
        holder.setInventory(inventory);

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(ColorUtil.text("<dark_gray> "));
        filler.setItemMeta(fillerMeta);
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }

        for (int i = 0; i < CustomEnchant.values().length && i < ENCHANT_SLOTS.size(); i++) {
            CustomEnchant enchant = CustomEnchant.values()[i];
            ItemStack icon = new ItemStack(enchant.getIcon());
            ItemMeta meta = icon.getItemMeta();
            int level = ItemUtil.getEnchantLevel(plugin, pickaxe, enchant);
            long nextOneCost = level >= enchant.getMaxLevel() ? 0 : plugin.getEnchantManager().getUpgradeCost(pickaxe, enchant, 1);
            long nextFiveCost = level >= enchant.getMaxLevel() ? 0 : plugin.getEnchantManager().getUpgradeCost(pickaxe, enchant, Math.min(5, enchant.getMaxLevel() - level));
            long nextTenCost = level >= enchant.getMaxLevel() ? 0 : plugin.getEnchantManager().getUpgradeCost(pickaxe, enchant, Math.min(10, enchant.getMaxLevel() - level));
            meta.displayName(ColorUtil.text((enchant.isVisual() ? "<light_purple><bold>" : "<aqua><bold>") + enchant.getDisplayName()));
            meta.lore(List.of(
                ColorUtil.text("<gray>" + enchant.getDescription()),
                ColorUtil.text("<dark_gray>"),
                ColorUtil.text("<gray>Level: <white>" + level + "<gray>/" + enchant.getMaxLevel()),
                ColorUtil.text("<gray>Type: <white>" + (enchant.isVisual() ? "Visual + Proc" : "Mining Proc")),
                ColorUtil.text("<gray>Tokens: <white>" + data.getTokens()),
                ColorUtil.text("<dark_gray>"),
                ColorUtil.text(level >= enchant.getMaxLevel() ? "<green>MAXED" : "<yellow>Left click: +1 <gray>(" + nextOneCost + " tokens)"),
                ColorUtil.text(level >= enchant.getMaxLevel() ? "<green>MAXED" : "<gold>Right click: +5 <gray>(" + nextFiveCost + " tokens)"),
                ColorUtil.text(level >= enchant.getMaxLevel() ? "<green>MAXED" : "<aqua>Shift click: +10 <gray>(" + nextTenCost + " tokens)")
            ));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.getPersistentDataContainer().set(ItemUtil.key(plugin, "menu_enchant"), PersistentDataType.STRING, enchant.getKey());
            icon.setItemMeta(meta);
            inventory.setItem(ENCHANT_SLOTS.get(i), icon);
        }

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.displayName(ColorUtil.text("<gold><bold>Pickaxe Overview"));
        infoMeta.lore(List.of(
            ColorUtil.text("<gray>Total Enchant Power: <white>" + ItemUtil.getTotalEnchantLevels(plugin, pickaxe)),
            ColorUtil.text("<gray>Blocks Mined: <white>" + ItemUtil.getBlocksMined(plugin, pickaxe)),
            ColorUtil.text("<gray>Total Value: <white>$" + plugin.getEconomyManager().format(ItemUtil.getValueSold(plugin, pickaxe)).replace("$", "")),
            ColorUtil.text("<gray>Use the GUI to grow one legendary pickaxe."),
            ColorUtil.text("<yellow>Per-item enchants mean every pickaxe can be unique.")
        ));
        info.setItemMeta(infoMeta);
        inventory.setItem(49, info);

        player.openInventory(inventory);
    }
}
