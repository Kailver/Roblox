package com.roblox.prisoncore.data;

import java.util.UUID;

public class PlayerData {
    private final UUID uniqueId;
    private double balance;
    private long tokens;
    private String rank;
    private int prestige;
    private boolean autoSell;
    private long backpackBlocks;
    private double backpackValue;

    public PlayerData(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getTokens() {
        return tokens;
    }

    public void setTokens(long tokens) {
        this.tokens = tokens;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getPrestige() {
        return prestige;
    }

    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    public boolean isAutoSell() {
        return autoSell;
    }

    public void setAutoSell(boolean autoSell) {
        this.autoSell = autoSell;
    }

    public long getBackpackBlocks() {
        return backpackBlocks;
    }

    public void addBackpackBlocks(long amount) {
        this.backpackBlocks += amount;
    }

    public double getBackpackValue() {
        return backpackValue;
    }

    public void addBackpackValue(double amount) {
        this.backpackValue += amount;
    }

    public void clearBackpack() {
        this.backpackBlocks = 0;
        this.backpackValue = 0D;
    }
}
