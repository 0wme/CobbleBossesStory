package com.kingpixel.cobblebosses.model;

import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - Enhanced reward system supporting both items and commands
 */
@Data
public class BossRewards {
    private List<Reward> rewards;

    public BossRewards() {
        this.rewards = new ArrayList<>();
    }

    public void giveRewards(ServerPlayerEntity player) {
        if (rewards == null || rewards.isEmpty()) {
            if (CobbleBosses.config.isDebug()) {
                CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "No rewards configured for boss");
            }
            return;
        }

        // Filtrer les récompenses valides
        List<Reward> validRewards = new ArrayList<>();
        for (Reward reward : rewards) {
            if (reward != null && reward.isValid()) {
                validRewards.add(reward);
            }
        }

        if (validRewards.isEmpty()) {
            if (CobbleBosses.config.isDebug()) {
                CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "No valid rewards found for boss");
            }
            return;
        }

        // Utiliser dropRolls de la config pour déterminer combien de récompenses donner
        int rollsCount = Math.max(1, CobbleBosses.config.getDropRolls());
        
        if (CobbleBosses.config.isDebug()) {
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, 
                "Giving " + rollsCount + " reward(s) to " + player.getName().getString());
        }

        // Faire plusieurs tirages selon dropRolls
        for (int i = 0; i < rollsCount; i++) {
            Reward selectedReward = selectRewardByWeight(validRewards);
            if (selectedReward != null) {
                selectedReward.giveToPlayer(player);
                
                if (CobbleBosses.config.isDebug()) {
                    CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, 
                        "Roll " + (i + 1) + "/" + rollsCount + ": " + selectedReward.getName());
                }
            }
        }
    }

    private Reward selectRewardByWeight(List<Reward> validRewards) {
        // Calculer le poids total
        double totalWeight = 0;
        for (Reward reward : validRewards) {
            totalWeight += reward.getWeight();
        }

        if (totalWeight <= 0) {
            // Si aucun poids, prendre au hasard
            return validRewards.get(Utils.RANDOM.nextInt(validRewards.size()));
        }

        // Sélection pondérée
        double random = Utils.RANDOM.nextDouble() * totalWeight;
        for (Reward reward : validRewards) {
            random -= reward.getWeight();
            if (random <= 0) {
                return reward;
            }
        }

        // Fallback - retourner le dernier
        return validRewards.get(validRewards.size() - 1);
    }

    @SuppressWarnings("unchecked")
    public void openMenu(ServerPlayerEntity player, 
                        java.util.function.Consumer<?> templateCallback,
                        java.util.function.Consumer<?> closeCallback) {
        // Pour le nouveau système, on pourrait implémenter un menu GUI custom
        // ou simplement log les récompenses disponibles
        if (CobbleBosses.config.isDebug()) {
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Opening rewards menu for " + player.getName().getString());
            if (rewards != null && !rewards.isEmpty()) {
                for (Reward reward : rewards) {
                    if (reward != null && reward.isValid()) {
                        CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, 
                            "Available reward: " + reward.getName() + " (weight: " + reward.getWeight() + ")");
                    }
                }
            }
        }
    }

    public void check() {
        if (rewards == null) {
            rewards = new ArrayList<>();
        }
        
        // Vérifier et nettoyer les récompenses invalides
        if (!rewards.isEmpty()) {
            rewards.removeIf(reward -> reward == null || !reward.isValid());
        }
    }
} 