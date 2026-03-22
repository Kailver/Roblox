package com.roblox.prisoncore.enchant;

import com.roblox.prisoncore.PrisonCorePlugin;
import com.roblox.prisoncore.data.PlayerData;
import com.roblox.prisoncore.util.ItemUtil;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EnchantManager {
    private final PrisonCorePlugin plugin;
    private final Random random = new Random();

    public EnchantManager(PrisonCorePlugin plugin) {
        this.plugin = plugin;
    }

    public long getUpgradeCost(ItemStack item, CustomEnchant enchant, int amount) {
        long total = 0;
        int currentLevel = ItemUtil.getEnchantLevel(plugin, item, enchant);
        for (int i = 1; i <= amount; i++) {
            total += (long) enchant.getBaseTokenCost() * (currentLevel + i);
        }
        return total;
    }

    public boolean upgrade(PlayerData data, ItemStack item, CustomEnchant enchant, int amount) {
        if (amount <= 0 || !ItemUtil.isPrisonPickaxe(plugin, item)) {
            return false;
        }
        int currentLevel = ItemUtil.getEnchantLevel(plugin, item, enchant);
        if (currentLevel >= enchant.getMaxLevel()) {
            return false;
        }
        int purchasable = Math.min(amount, enchant.getMaxLevel() - currentLevel);
        long cost = getUpgradeCost(item, enchant, purchasable);
        if (data.getTokens() < cost) {
            return false;
        }
        data.setTokens(data.getTokens() - cost);
        ItemUtil.setEnchantLevel(plugin, item, enchant, currentLevel + purchasable);
        ItemUtil.refreshPickaxe(plugin, item);
        return true;
    }

    public double applyValueModifiers(ItemStack item, Block block, double baseValue) {
        double value = baseValue;
        value *= 1.0 + (ItemUtil.getEnchantLevel(plugin, item, CustomEnchant.FORTUNE) * 0.01);
        value *= 1.0 + (ItemUtil.getEnchantLevel(plugin, item, CustomEnchant.GREED) * 0.0045);
        value *= 1.0 + Math.min(1.35, ItemUtil.getTotalEnchantLevels(plugin, item) * 0.0008);
        if (isRareOre(block.getType())) {
            value *= 1.0 + (ItemUtil.getEnchantLevel(plugin, item, CustomEnchant.GEM_FINDER) * 0.0065);
        }
        if (proc(item, CustomEnchant.JACKPOT, 0.0015, 0.12)) {
            value *= 2.0 + (random.nextDouble() * 2.5);
        }
        if (proc(item, CustomEnchant.AURORA, 0.004, 0.20)) {
            spawnVisual(block, Particle.ENCHANT, 25);
            value *= 1.20;
        }
        if (proc(item, CustomEnchant.DISCO, 0.004, 0.20)) {
            spawnVisual(block, Particle.NOTE, 30);
            value *= 1.15;
        }
        if (proc(item, CustomEnchant.LIGHTNING, 0.0035, 0.18)) {
            block.getWorld().strikeLightningEffect(block.getLocation());
            value *= 1.25;
        }
        if (proc(item, CustomEnchant.FIREBURST, 0.004, 0.20)) {
            spawnVisual(block, Particle.FLAME, 35);
            value *= 1.18;
        }
        if (proc(item, CustomEnchant.TREASURE_HUNTER, 0.003, 0.18)) {
            value *= 1.10;
        }
        return value;
    }

    public long applyTokenModifiers(ItemStack item, Block block, long baseTokens) {
        long tokens = baseTokens;
        int tokenFinder = ItemUtil.getEnchantLevel(plugin, item, CustomEnchant.TOKEN_FINDER);
        if (tokenFinder > 0 && random.nextDouble() <= Math.min(0.75, tokenFinder * 0.0055)) {
            tokens += Math.max(1, tokenFinder / 7L);
        }
        if (proc(item, CustomEnchant.COMET, 0.004, 0.20)) {
            spawnVisual(block, Particle.END_ROD, 30);
            tokens += 3;
        }
        if (proc(item, CustomEnchant.CRYSTAL, 0.004, 0.20)) {
            spawnVisual(block, Particle.WAX_ON, 24);
            tokens += 2 + Math.max(1, ItemUtil.getEnchantLevel(plugin, item, CustomEnchant.CRYSTAL) / 20);
        }
        return tokens;
    }

    public void applyPlayerBuffs(Player player, ItemStack item) {
        if (proc(item, CustomEnchant.HASTE, 0.01, 0.50)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 6, 1, true, false, true));
        }
        if (proc(item, CustomEnchant.FRENZY, 0.006, 0.35)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 5, 2, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1, true, false, true));
        }
        if (proc(item, CustomEnchant.BLESSING, 0.003, 0.20)) {
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 1.0));
        }
    }

    public int bonusExperience(ItemStack item) {
        int level = ItemUtil.getEnchantLevel(plugin, item, CustomEnchant.EXPERIENCE);
        if (level <= 0) {
            return 0;
        }
        return random.nextDouble() <= Math.min(0.60, level * 0.006) ? Math.max(1, level / 10) : 0;
    }

    public Set<Block> collectExtraBlocks(ItemStack item, Block origin) {
        Set<Block> blocks = new HashSet<>();
        maybeAddJackhammer(item, origin, blocks);
        maybeAddLaser(item, origin, blocks);
        maybeAddExplosion(item, origin, blocks);
        maybeAddDrill(item, origin, blocks);
        maybeAddShockwave(item, origin, blocks);
        maybeAddMeteor(item, origin, blocks);
        maybeAddThunder(item, origin, blocks);
        maybeAddVeinMiner(item, origin, blocks);
        maybeAddNuclear(item, origin, blocks);
        return blocks;
    }

    private void maybeAddJackhammer(ItemStack item, Block origin, Set<Block> blocks) {
        if (!proc(item, CustomEnchant.JACKHAMMER, 0.0025, 0.16)) {
            return;
        }
        for (int y = origin.getY() - 1; y >= Math.max(origin.getWorld().getMinHeight(), origin.getY() - 7); y--) {
            blocks.add(origin.getWorld().getBlockAt(origin.getX(), y, origin.getZ()));
        }
    }

    private void maybeAddLaser(ItemStack item, Block origin, Set<Block> blocks) {
        if (!proc(item, CustomEnchant.LASER, 0.003, 0.18)) {
            return;
        }
        spawnVisual(origin, Particle.ELECTRIC_SPARK, 28);
        for (int x = origin.getX() - 3; x <= origin.getX() + 3; x++) {
            blocks.add(origin.getWorld().getBlockAt(x, origin.getY(), origin.getZ()));
        }
    }

    private void maybeAddExplosion(ItemStack item, Block origin, Set<Block> blocks) {
        if (!proc(item, CustomEnchant.EXPLOSION, 0.0025, 0.18)) {
            return;
        }
        spawnVisual(origin, Particle.EXPLOSION, 3);
        addCube(origin, 1, blocks);
    }

    private void maybeAddDrill(ItemStack item, Block origin, Set<Block> blocks) {
        if (!proc(item, CustomEnchant.DRILL, 0.002, 0.14)) {
            return;
        }
        for (int x = origin.getX() - 1; x <= origin.getX() + 1; x++) {
            for (int y = origin.getY() - 1; y <= origin.getY() + 1; y++) {
                for (int z = origin.getZ() - 1; z <= origin.getZ() + 1; z++) {
                    blocks.add(origin.getWorld().getBlockAt(x, y, z));
                }
            }
        }
    }

    private void maybeAddShockwave(ItemStack item, Block origin, Set<Block> blocks) {
        if (!proc(item, CustomEnchant.SHOCKWAVE, 0.0025, 0.16)) {
            return;
        }
        spawnVisual(origin, Particle.SONIC_BOOM, 1);
        int radius = 2;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x) + Math.abs(z) <= radius + 1) {
                    blocks.add(origin.getWorld().getBlockAt(origin.getX() + x, origin.getY(), origin.getZ() + z));
                }
            }
        }
    }

    private void maybeAddMeteor(ItemStack item, Block origin, Set<Block> blocks) {
        if (!proc(item, CustomEnchant.METEOR, 0.0025, 0.14)) {
            return;
        }
        spawnVisual(origin, Particle.LAVA, 22);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                blocks.add(origin.getWorld().getBlockAt(origin.getX() + x, origin.getY(), origin.getZ() + z));
            }
        }
    }

    private void maybeAddThunder(ItemStack item, Block origin, Set<Block> blocks) {
        if (!proc(item, CustomEnchant.THUNDER, 0.003, 0.18)) {
            return;
        }
        origin.getWorld().strikeLightningEffect(origin.getLocation());
        for (int y = origin.getY() - 1; y <= origin.getY() + 1; y++) {
            blocks.add(origin.getWorld().getBlockAt(origin.getX(), y, origin.getZ()));
        }
        for (int x = origin.getX() - 1; x <= origin.getX() + 1; x++) {
            blocks.add(origin.getWorld().getBlockAt(x, origin.getY(), origin.getZ()));
        }
    }

    private void maybeAddVeinMiner(ItemStack item, Block origin, Set<Block> blocks) {
        int level = ItemUtil.getEnchantLevel(plugin, item, CustomEnchant.VEIN_MINER);
        if (level <= 0 || random.nextDouble() > Math.min(0.18, level * 0.0015)) {
            return;
        }
        int limit = Math.min(24, 6 + (level / 8));
        Queue<Block> queue = new ArrayDeque<>();
        Set<Block> visited = new HashSet<>();
        queue.add(origin);
        while (!queue.isEmpty() && blocks.size() < limit) {
            Block current = queue.poll();
            if (!visited.add(current)) {
                continue;
            }
            if (current.getType() != origin.getType()) {
                continue;
            }
            blocks.add(current);
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }
                        queue.add(current.getWorld().getBlockAt(current.getX() + x, current.getY() + y, current.getZ() + z));
                    }
                }
            }
        }
    }

    private void maybeAddNuclear(ItemStack item, Block origin, Set<Block> blocks) {
        if (!proc(item, CustomEnchant.NUCLEAR, 0.0015, 0.10)) {
            return;
        }
        spawnVisual(origin, Particle.EXPLOSION_EMITTER, 1);
        addCube(origin, 2, blocks);
    }

    private void addCube(Block origin, int radius, Set<Block> blocks) {
        for (int x = origin.getX() - radius; x <= origin.getX() + radius; x++) {
            for (int y = origin.getY() - radius; y <= origin.getY() + radius; y++) {
                for (int z = origin.getZ() - radius; z <= origin.getZ() + radius; z++) {
                    blocks.add(origin.getWorld().getBlockAt(x, y, z));
                }
            }
        }
    }

    private void spawnVisual(Block block, Particle particle, int count) {
        block.getWorld().spawnParticle(particle, block.getLocation().add(0.5, 0.5, 0.5), count, 0.45, 0.45, 0.45, 0.01);
    }

    private boolean proc(ItemStack item, CustomEnchant enchant, double scale, double cap) {
        int level = ItemUtil.getEnchantLevel(plugin, item, enchant);
        return level > 0 && random.nextDouble() <= Math.min(cap, level * scale);
    }

    private boolean isRareOre(Material material) {
        return material.name().contains("DIAMOND") || material.name().contains("EMERALD") || material.name().contains("ANCIENT_DEBRIS");
    }
}
