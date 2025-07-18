package com.kingpixel.cobblebosses.model;

import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Data
public class Reward {
    private String type;
    private int customModelData;
    private String value;
    private String name;
    private String identifier;
    private int quantityMin;
    private int quantityMax;
    private double weight;

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

            try {
                Identifier itemId = Identifier.of(identifier);
                var item = Registries.ITEM.get(itemId);
            
                if (item != null && item != net.minecraft.item.Items.AIR) {
                    ItemStack itemStack = new ItemStack(item, quantity);
                    
                    if (!player.getInventory().insertStack(itemStack)) {
                        player.dropItem(itemStack, false);
                    }
                    
                    if (CobbleBosses.config.isDebug()) {
                        CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, 
                            "Gave item via inventory: " + identifier + " x" + quantity + " to " + player.getName().getString());
                    }
                    return;
                }
            } catch (Exception e) {
                if (CobbleBosses.config.isDebug()) {
                    CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, 
                        "Failed to give item via inventory, trying command: " + e.getMessage());
                }
            }

            String giveCommand = "give " + player.getName().getString() + " " + identifier + " " + quantity;
            
            if (CobbleBosses.config.isDebug()) {
                CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Executing command: " + giveCommand);
            }

            if (CobbleBosses.server != null) {
                CobbleBosses.server.getCommandManager().executeWithPrefix(
                    CobbleBosses.server.getCommandSource(), 
                    giveCommand
                );

                if (CobbleBosses.config.isDebug()) {
                    CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, 
                        "Executed give command: " + identifier + " x" + quantity + " to " + player.getName().getString());
                }
            } else {
                CobbleUtils.LOGGER.error(CobbleBosses.MOD_ID, "Server is null, cannot execute give command");
            }

        } catch (Exception e) {
            CobbleUtils.LOGGER.error(CobbleBosses.MOD_ID, "Error giving item reward: " + identifier + " - " + e.getMessage());
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

            String processedCommand = value.replace("{player}", player.getName().getString())
                                           .replace("%player%", player.getName().getString())
                                           .replace("{uuid}", player.getUuidAsString())
                                           .replace("%uuid%", player.getUuidAsString());
            
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