package com.roblox.prisoncore.enchant;

import java.util.Arrays;
import java.util.Locale;
import org.bukkit.Material;

public enum CustomEnchant {
    FORTUNE("fortune", "Fortune", Material.GOLD_INGOT, 500, 18, false, "Boosts the sell value of every block you mine."),
    GREED("greed", "Greed", Material.SUNFLOWER, 400, 14, false, "Stacks an extra flat cash multiplier."),
    TOKEN_FINDER("token_finder", "Token Finder", Material.AMETHYST_SHARD, 350, 12, false, "Find extra tokens while mining."),
    JACKHAMMER("jackhammer", "Jackhammer", Material.IRON_PICKAXE, 250, 40, false, "Breaks a vertical tunnel beneath the block."),
    LASER("laser", "Laser", Material.REDSTONE, 250, 40, true, "Cuts a bright mining beam through the row."),
    EXPLOSION("explosion", "Explosion", Material.TNT, 200, 45, true, "Blasts a compact cube around the proc block."),
    VEIN_MINER("vein_miner", "Vein Miner", Material.DIAMOND_ORE, 200, 34, false, "Chains through nearby blocks of the same type."),
    DRILL("drill", "Drill", Material.NETHERITE_PICKAXE, 175, 48, false, "Breaks a 3x3 section instantly."),
    NUCLEAR("nuclear", "Nuclear", Material.RESPAWN_ANCHOR, 100, 70, true, "Rarely clears a huge area for massive value."),
    SHOCKWAVE("shockwave", "Shockwave", Material.NAUTILUS_SHELL, 200, 36, true, "Sends out a circular pulse that breaks blocks."),
    METEOR("meteor", "Meteor", Material.MAGMA_CREAM, 150, 38, true, "Calls down a meteor-style impact around the block."),
    LIGHTNING("lightning", "Lightning", Material.LIGHTNING_ROD, 150, 28, true, "Triggers lightning visuals and a bonus payout proc."),
    FIREBURST("fireburst", "Fireburst", Material.BLAZE_POWDER, 150, 26, true, "Ignites the area with a fiery proc and extra money."),
    COMET("comet", "Comet", Material.END_ROD, 150, 24, true, "Leaves a comet trail and grants extra tokens."),
    AURORA("aurora", "Aurora", Material.PRISMARINE_CRYSTALS, 150, 24, true, "Paints the mine with aurora particles and payout bonus."),
    DISCO("disco", "Disco", Material.NOTE_BLOCK, 150, 22, true, "Celebration particles with a proc-based reward boost."),
    HASTE("haste", "Haste", Material.SUGAR, 250, 18, false, "Frequently refreshes your haste effect."),
    MOMENTUM("momentum", "Momentum", Material.FEATHER, 400, 16, false, "Increases payout as your pickaxe gains levels."),
    JACKPOT("jackpot", "Jackpot", Material.EMERALD_BLOCK, 120, 55, false, "Rare chance to heavily multiply your cash reward."),
    TREASURE_HUNTER("treasure_hunter", "Treasure Hunter", Material.CHEST, 250, 25, false, "Adds more bonus rewards on big procs."),
    EXPERIENCE("experience", "Experience", Material.EXPERIENCE_BOTTLE, 350, 14, false, "Generates extra vanilla XP while mining."),
    BLESSING("blessing", "Blessing", Material.GOLDEN_APPLE, 200, 18, false, "Occasionally heals you during heavy mining."),
    FRENZY("frenzy", "Frenzy", Material.RABBIT_FOOT, 200, 20, false, "Gives short bursts of speed and haste."),
    GEM_FINDER("gem_finder", "Gem Finder", Material.DIAMOND, 220, 30, false, "Greatly boosts payouts from rare ores."),
    CRYSTAL("crystal", "Crystal", Material.AMETHYST_CLUSTER, 180, 24, true, "Creates crystal blooms and bonus token bursts."),
    THUNDER("thunder", "Thunder", Material.TRIDENT, 120, 32, true, "Chain-lightning style visuals with area break support.");

    private final String key;
    private final String displayName;
    private final Material icon;
    private final int maxLevel;
    private final int baseTokenCost;
    private final boolean visual;
    private final String description;

    CustomEnchant(String key, String displayName, Material icon, int maxLevel, int baseTokenCost, boolean visual, String description) {
        this.key = key;
        this.displayName = displayName;
        this.icon = icon;
        this.maxLevel = maxLevel;
        this.baseTokenCost = baseTokenCost;
        this.visual = visual;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getBaseTokenCost() {
        return baseTokenCost;
    }

    public boolean isVisual() {
        return visual;
    }

    public String getDescription() {
        return description;
    }

    public static CustomEnchant fromInput(String input) {
        String normalized = input.toLowerCase(Locale.ROOT).replace(" ", "").replace("_", "");
        return Arrays.stream(values())
            .filter(enchant -> enchant.key.equalsIgnoreCase(input)
                || enchant.name().equalsIgnoreCase(input)
                || enchant.displayName.replace(" ", "").equalsIgnoreCase(normalized))
            .findFirst()
            .orElse(null);
    }
}
