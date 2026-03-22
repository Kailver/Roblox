package com.roblox.prisoncore;

import com.roblox.prisoncore.command.AutosellCommand;
import com.roblox.prisoncore.command.BackpackCommand;
import com.roblox.prisoncore.command.BalanceCommand;
import com.roblox.prisoncore.command.EnchantCommand;
import com.roblox.prisoncore.command.MineCommand;
import com.roblox.prisoncore.command.PayCommand;
import com.roblox.prisoncore.command.PickaxeCommand;
import com.roblox.prisoncore.command.PrisonCommand;
import com.roblox.prisoncore.command.PrestigeCommand;
import com.roblox.prisoncore.command.RankupCommand;
import com.roblox.prisoncore.command.SellAllCommand;
import com.roblox.prisoncore.command.TokensCommand;
import com.roblox.prisoncore.data.DataManager;
import com.roblox.prisoncore.economy.EconomyManager;
import com.roblox.prisoncore.enchant.EnchantManager;
import com.roblox.prisoncore.listener.EnchantMenuListener;
import com.roblox.prisoncore.listener.PlayerConnectionListener;
import com.roblox.prisoncore.listener.PrisonBlockListener;
import com.roblox.prisoncore.menu.EnchantMenu;
import com.roblox.prisoncore.mine.MineManager;
import com.roblox.prisoncore.rank.RankManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonCorePlugin extends JavaPlugin {
    private DataManager dataManager;
    private EconomyManager economyManager;
    private RankManager rankManager;
    private EnchantManager enchantManager;
    private MineManager mineManager;
    private EnchantMenu enchantMenu;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.dataManager = new DataManager(this);
        this.economyManager = new EconomyManager(getConfig().getString("currency-symbol", "$"));
        this.rankManager = new RankManager(this);
        this.enchantManager = new EnchantManager(this);
        this.mineManager = new MineManager(this);
        this.enchantMenu = new EnchantMenu(this);

        registerCommands();
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PrisonBlockListener(this), this);
        getServer().getPluginManager().registerEvents(new EnchantMenuListener(this), this);
    }

    @Override
    public void onDisable() {
        if (mineManager != null) {
            mineManager.shutdown();
        }
        if (dataManager != null) {
            dataManager.saveAll();
        }
    }

    private void registerCommands() {
        bind("prison", new PrisonCommand(this));
        bind("rankup", new RankupCommand(this));
        bind("prestige", new PrestigeCommand(this));
        bind("balance", new BalanceCommand(this));
        bind("pay", new PayCommand(this));
        bind("tokens", new TokensCommand(this));
        bind("mine", new MineCommand(this));
        bind("pickaxe", new PickaxeCommand(this));
        bind("enchant", new EnchantCommand(this));
        bind("sellall", new SellAllCommand(this));
        bind("backpack", new BackpackCommand(this));
        bind("autosell", new AutosellCommand(this));
    }

    private void bind(String commandName, CommandExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            getLogger().warning("Command missing from plugin.yml: " + commandName);
            return;
        }
        command.setExecutor(executor);
        if (executor instanceof TabCompleter completer) {
            command.setTabCompleter(completer);
        }
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public EnchantManager getEnchantManager() {
        return enchantManager;
    }

    public MineManager getMineManager() {
        return mineManager;
    }

    public EnchantMenu getEnchantMenu() {
        return enchantMenu;
    }
}
