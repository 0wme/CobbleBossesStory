package com.kingpixel.cobblebosses.model;

import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class BossRewards {
    private List<Reward> rewards;

    public BossRewards() {
        this.rewards = new ArrayList<>();
    }

    public BossRewards(List<Reward> rewards) {
        this.rewards = rewards != null ? rewards : new ArrayList<>();
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards != null ? rewards : new ArrayList<>();
    }

    public List<Reward> getValidRewards() {
        if (rewards == null) return new ArrayList<>();
        
        return rewards.stream()
                .filter(reward -> reward != null && reward.isValid())
                .toList();
    }

    public void giveRandomRewards(ServerPlayerEntity player) {
        List<Reward> validRewards = getValidRewards();
        if (validRewards.isEmpty()) {
            if (CobbleBosses.config.isDebug()) {
                CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "No valid rewards available for boss");
            }
            return;
        }

        int dropRolls = CobbleBosses.config.getDropRolls();
        
        for (int i = 0; i < dropRolls; i++) {
            if (CobbleBosses.config.isDebug()) {
                CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Roll " + (i + 1) + "/" + dropRolls);
            }
            
            Reward selectedReward = selectRandomReward(validRewards);
            if (selectedReward != null) {
                selectedReward.giveToPlayer(player);
                if (CobbleBosses.config.isDebug()) {
                    CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, 
                        "Gave reward: " + selectedReward.getName() + " (type: " + selectedReward.getType() + ")");
                }
            }
        }
    }

    private Reward selectRandomReward(List<Reward> validRewards) {
        if (validRewards.isEmpty()) return null;

        double totalWeight = validRewards.stream()
                .mapToDouble(Reward::getWeight)
                .sum();

        if (totalWeight <= 0) {
            return validRewards.get(Utils.RANDOM.nextInt(validRewards.size()));
        }

        double randomValue = Utils.RANDOM.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (Reward reward : validRewards) {
            currentWeight += reward.getWeight();
            if (randomValue <= currentWeight) {
                return reward;
            }
        }

        return validRewards.get(validRewards.size() - 1);
    }

    public void openGui(ServerPlayerEntity player) {
        if (CobbleBosses.config.isDebug()) {
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Opening rewards GUI for " + player.getName().getString());
        }
    }

    private void validateRewards() {
        if (rewards == null) return;
        
        rewards.removeIf(reward -> reward == null || !reward.isValid());
        
        if (CobbleBosses.config.isDebug()) {
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, 
                "Validated rewards. Valid count: " + rewards.size());
        }
    }
} 