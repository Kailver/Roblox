package com.roblox.prisoncore.economy;

import com.roblox.prisoncore.data.PlayerData;
import java.text.DecimalFormat;

public class EconomyManager {
    private static final DecimalFormat FORMAT = new DecimalFormat("#,##0.00");
    private final String currencySymbol;

    public EconomyManager(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public void deposit(PlayerData data, double amount) {
        data.setBalance(data.getBalance() + amount);
    }

    public boolean withdraw(PlayerData data, double amount) {
        if (data.getBalance() < amount) {
            return false;
        }
        data.setBalance(data.getBalance() - amount);
        return true;
    }

    public String format(double value) {
        return currencySymbol + FORMAT.format(value);
    }
}
