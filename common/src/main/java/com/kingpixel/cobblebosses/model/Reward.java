package com.kingpixel.cobblebosses.model;

import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - Unified reward system supporting both items and commands
 */
@Data
public class Reward {
    private String type; // "item" ou "command"
    private int customModelData;
    private String value; // Pour les commandes, c'est la commande à exécuter
    private String name;
    private String identifier;
    private int quantityMin;
    private int quantityMax;
    private double weight;

    public Reward() {
        this.type = "item";
        this.customModelData = -1;
        this.value = "";
        this.name = "";
        this.identifier = "";
        this.quantityMin = 1;
        this.quantityMax = 1;
        this.weight = 1.0;
    }

    public void giveToPlayer(ServerPlayerEntity player) {
        if (type == null) {
            if (CobbleBosses.config.isDebug()) {
                CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "Reward type is null for: " + name);
            }
            return;
        }

        switch (type.toLowerCase()) {
            case "item":
                giveItem(player);
                break;
            case "command":
                executeCommand(player);
                break;
            default:
                if (CobbleBosses.config.isDebug()) {
                    CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "Unknown reward type: " + type);
                }
                break;
        }
    }

    private void giveItem(ServerPlayerEntity player) {
        try {
            if (identifier == null || identifier.trim().isEmpty()) {
                if (CobbleBosses.config.isDebug()) {
                    CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "Empty identifier for item reward: " + name);
                }
                return;
            }

            int quantity = quantityMin;
            if (quantityMax > quantityMin) {
                quantity = Utils.RANDOM.nextInt(quantityMin, quantityMax + 1);
            }

            // Construire la commande give pour donner l'item
            String giveCommand = "give " + player.getName().getString() + " " + identifier + " " + quantity;
            
            // Ajouter les NBT si nécessaire
            if (customModelData > 0 || (name != null && !name.trim().isEmpty())) {
                StringBuilder nbtBuilder = new StringBuilder();
                nbtBuilder.append("{");
                
                boolean hasNbt = false;
                if (customModelData > 0) {
                    nbtBuilder.append("CustomModelData:").append(customModelData);
                    hasNbt = true;
                }
                
                if (name != null && !name.trim().isEmpty()) {
                    if (hasNbt) nbtBuilder.append(",");
                    nbtBuilder.append("display:{Name:'{\"text\":\"").append(name).append("\"}'}");
                }
                
                nbtBuilder.append("}");
                giveCommand += nbtBuilder.toString();
            }

            // Exécuter la commande give
            if (CobbleBosses.server != null) {
                CobbleBosses.server.getCommandManager().executeWithPrefix(
                    CobbleBosses.server.getCommandSource(), 
                    giveCommand
                );

                if (CobbleBosses.config.isDebug()) {
                    CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, 
                        "Gave item " + identifier + " x" + quantity + " to " + player.getName().getString());
                }
            }

        } catch (Exception e) {
            CobbleUtils.LOGGER.error(CobbleBosses.MOD_ID, "Error giving item reward: " + identifier);
            e.printStackTrace();
        }
    }

    private void executeCommand(ServerPlayerEntity player) {
        try {
            if (value == null || value.trim().isEmpty()) {
                if (CobbleBosses.config.isDebug()) {
                    CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "Empty command for reward: " + name);
                }
                return;
            }

            // Remplacer les placeholders
            String processedCommand = value.replace("{player}", player.getName().getString())
                                           .replace("%player%", player.getName().getString())
                                           .replace("{uuid}", player.getUuidAsString())
                                           .replace("%uuid%", player.getUuidAsString());

            // Exécuter la commande depuis la console
            if (CobbleBosses.server != null) {
                CobbleBosses.server.getCommandManager().executeWithPrefix(
                    CobbleBosses.server.getCommandSource(), 
                    processedCommand
                );

                if (CobbleBosses.config.isDebug()) {
                    CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, 
                        "Executed command for " + player.getName().getString() + ": " + processedCommand);
                }
            }

        } catch (Exception e) {
            CobbleUtils.LOGGER.error(CobbleBosses.MOD_ID, "Error executing command reward: " + value);
            e.printStackTrace();
        }
    }

    public boolean isValid() {
        if (type == null || type.trim().isEmpty()) return false;
        if (weight <= 0) return false;
        
        switch (type.toLowerCase()) {
            case "item":
                return identifier != null && !identifier.trim().isEmpty();
            case "command":
                return value != null && !value.trim().isEmpty();
            default:
                return false;
        }
    }
} 