package com.roblox.prisoncore.util;

import com.roblox.prisoncore.enchant.CustomEnchant;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemUtil {
    private static final DecimalFormat DECIMAL = new DecimalFormat("#,##0.00");

    private ItemUtil() {
    }

    public static ItemStack createStarterPickaxe(JavaPlugin plugin, String ownerName) {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorUtil.text("<gradient:aqua:blue><bold>Cosmic Prison Pickaxe</bold></gradient>"));
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key(plugin, "prison_pickaxe"), PersistentDataType.BYTE, (byte) 1);
        container.set(key(plugin, "owner"), PersistentDataType.STRING, ownerName);
        container.set(key(plugin, "blocks_mined"), PersistentDataType.LONG, 0L);
        container.set(key(plugin, "value_sold"), PersistentDataType.DOUBLE, 0D);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        refreshPickaxe(plugin, item);
        return item;
    }

    public static boolean isPrisonPickaxe(JavaPlugin plugin, ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        Byte value = item.getItemMeta().getPersistentDataContainer().get(key(plugin, "prison_pickaxe"), PersistentDataType.BYTE);
        return value != null && value == 1;
    }

    public static int getEnchantLevel(JavaPlugin plugin, ItemStack item, CustomEnchant enchant) {
        if (!isPrisonPickaxe(plugin, item)) {
            return 0;
        }
        Integer level = item.getItemMeta().getPersistentDataContainer().get(key(plugin, "enchant_" + enchant.getKey()), PersistentDataType.INTEGER);
        return level == null ? 0 : level;
    }

    public static void setEnchantLevel(JavaPlugin plugin, ItemStack item, CustomEnchant enchant, int level) {
        if (!isPrisonPickaxe(plugin, item)) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key(plugin, "enchant_" + enchant.getKey()), PersistentDataType.INTEGER, level);
        item.setItemMeta(meta);
    }

    public static long getBlocksMined(JavaPlugin plugin, ItemStack item) {
        if (!isPrisonPickaxe(plugin, item)) {
            return 0L;
        }
        Long value = item.getItemMeta().getPersistentDataContainer().get(key(plugin, "blocks_mined"), PersistentDataType.LONG);
        return value == null ? 0L : value;
    }

    public static void addBlocksMined(JavaPlugin plugin, ItemStack item, long amount) {
        if (!isPrisonPickaxe(plugin, item)) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        long current = getBlocksMined(plugin, item);
        container.set(key(plugin, "blocks_mined"), PersistentDataType.LONG, current + amount);
        item.setItemMeta(meta);
    }

    public static double getValueSold(JavaPlugin plugin, ItemStack item) {
        if (!isPrisonPickaxe(plugin, item)) {
            return 0D;
        }
        Double value = item.getItemMeta().getPersistentDataContainer().get(key(plugin, "value_sold"), PersistentDataType.DOUBLE);
        return value == null ? 0D : value;
    }

    public static void addValueSold(JavaPlugin plugin, ItemStack item, double amount) {
        if (!isPrisonPickaxe(plugin, item)) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        double current = getValueSold(plugin, item);
        container.set(key(plugin, "value_sold"), PersistentDataType.DOUBLE, current + amount);
        item.setItemMeta(meta);
    }

    public static int getTotalEnchantLevels(JavaPlugin plugin, ItemStack item) {
        int total = 0;
        for (CustomEnchant enchant : CustomEnchant.values()) {
            total += getEnchantLevel(plugin, item, enchant);
        }
        return total;
    }

    public static void refreshPickaxe(JavaPlugin plugin, ItemStack item) {
        if (!isPrisonPickaxe(plugin, item)) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.lore(buildLore(plugin, item));
        item.setItemMeta(meta);
    }

    public static NamespacedKey key(JavaPlugin plugin, String value) {
        return new NamespacedKey(plugin, value);
    }

    private static List<Component> buildLore(JavaPlugin plugin, ItemStack item) {
        List<Component> lore = new ArrayList<>();
        lore.add(ColorUtil.text("<gray>Blocks Mined: <yellow>" + getBlocksMined(plugin, item)));
        lore.add(ColorUtil.text("<gray>Total Value Generated: <gold>$" + DECIMAL.format(getValueSold(plugin, item))));
        lore.add(ColorUtil.text("<gray>Total Enchant Power: <aqua>" + getTotalEnchantLevels(plugin, item)));
        lore.add(ColorUtil.text("<dark_gray>"));

        List<CustomEnchant> active = new ArrayList<>();
        for (CustomEnchant enchant : CustomEnchant.values()) {
            if (getEnchantLevel(plugin, item, enchant) > 0) {
                active.add(enchant);
            }
        }
        active.stream()
            .sorted(Comparator.comparing(CustomEnchant::getDisplayName))
            .forEach(enchant -> lore.add(ColorUtil.text((enchant.isVisual() ? "<light_purple>" : "<aqua>") + enchant.getDisplayName() + " <gray>" + getEnchantLevel(plugin, item, enchant))));

        if (active.isEmpty()) {
            lore.add(ColorUtil.text("<gray>No enchants yet - use <yellow>/enchant</yellow> or <yellow>/pickaxe menu</yellow>."));
        }

        lore.add(ColorUtil.text("<dark_gray>"));
        lore.add(ColorUtil.text("<yellow>Hold this pickaxe to open the enchant GUI."));
        return lore;
    }
}
