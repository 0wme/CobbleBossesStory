package com.kingpixel.cobblebosses.model;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobblebosses.config.BossesConfig;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class AutoSpawner {
    private static Timer spawnTimer = null;
    private static boolean isRunning = false;

    public static void startAutoSpawning() {
        if (CobbleBosses.config.getSpawnInterval() <= 0) {
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "AutoSpawn disabled (spawnInterval = 0 in config)");
            return;
        }
        
        stopAutoSpawning();
        
        CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "🚀 CobbleBosses AutoSpawn activated! Every " + CobbleBosses.config.getSpawnInterval() + " minutes, " + (CobbleBosses.config.getBossSpawnChance() * 100) + "% chance of boss spawn");
        
        isRunning = true;
        long intervalMillis = CobbleBosses.config.getSpawnInterval() * 60L * 1000L; // Convert minutes to milliseconds
        
        spawnTimer = new Timer("CobbleBosses-AutoSpawn", true);
        
        spawnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (CobbleBosses.server != null) {
                        CobbleBosses.server.execute(() -> {
                            try {
                                attemptBossSpawn();
                            } catch (Exception e) {
                                CobbleUtils.LOGGER.error(CobbleBosses.MOD_ID, "Error in auto spawn execution: " + e.getMessage());
                            }
                        });
                    }
                } catch (Exception e) {
                    CobbleUtils.LOGGER.error(CobbleBosses.MOD_ID, "Error in auto spawn timer: " + e.getMessage());
                }
            }
        }, 30000L, intervalMillis); // Start after 30 seconds, then repeat
        
        CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "✅ AutoSpawn system ready! Next boss check in " + CobbleBosses.config.getSpawnInterval() + " minutes.");
    }

    public static void stopAutoSpawning() {
        if (spawnTimer != null) {
            spawnTimer.cancel();
            spawnTimer = null;
        }
        isRunning = false;
        CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "AutoSpawner stopped");
    }

    public static boolean isRunning() {
        return isRunning && spawnTimer != null;
    }

    public static void restart() {
        CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Restarting AutoSpawner...");
        stopAutoSpawning();
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        startAutoSpawning();
    }

    private static void attemptBossSpawn() {
        try {
            // Check spawn chance first
            double random = ThreadLocalRandom.current().nextDouble();
            double spawnChance = CobbleBosses.config.getBossSpawnChance();
            
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "AutoSpawn check: " + String.format("%.1f", random * 100) + "% vs " + String.format("%.1f", spawnChance * 100) + "% chance");
            
            if (random > spawnChance) {
                CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "No boss spawned this interval (chance failed)");
                return;
            }
            
            // Get online players
            List<ServerPlayerEntity> players = CobbleBosses.server.getPlayerManager().getPlayerList();
            if (players.isEmpty()) {
                CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "No players online, skipping boss spawn");
                return;
            }
            
            // Select random player
            ServerPlayerEntity randomPlayer = players.get(ThreadLocalRandom.current().nextInt(players.size()));
            ServerWorld world = randomPlayer.getServerWorld();
            
            // Check if world is blacklisted
            String worldName = world.getRegistryKey().getValue().toString();
            if (CobbleBosses.config.getBlackListWorlds().contains(worldName)) {
                CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "World " + worldName + " is blacklisted, skipping spawn");
                return;
            }
            
            // Find spawn position
            Vec3d spawnPos = findSafeSpawnPosition(randomPlayer);
            if (spawnPos == null) {
                CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "Could not find safe spawn position near " + randomPlayer.getName().getString());
                return;
            }
            
            // Select random boss
            Boss randomBoss = BossesConfig.getRandomBoss();
            if (randomBoss == null) {
                CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "No boss configuration available for spawn");
                return;
            }
            
            // Generate Pokemon and spawn boss
            var pokemon = PokemonProperties.Companion.parse("random").create();
            randomBoss.spawn(world, spawnPos, pokemon);
            
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "AutoSpawned boss: " + randomBoss.getId() + " near " + randomPlayer.getName().getString());
            
        } catch (Exception e) {
            CobbleUtils.LOGGER.error(CobbleBosses.MOD_ID, "Error in attemptBossSpawn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Vec3d findSafeSpawnPosition(ServerPlayerEntity player) {
        Vec3d playerPos = player.getPos();
        
        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                double distance = ThreadLocalRandom.current().nextDouble(30, 80);
                double angle = ThreadLocalRandom.current().nextDouble() * 2 * Math.PI;
                
                double spawnX = playerPos.x + Math.cos(angle) * distance;
                double spawnZ = playerPos.z + Math.sin(angle) * distance;
                
                // Check world bounds
                if (spawnX < -30000000 || spawnX > 30000000 || spawnZ < -30000000 || spawnZ > 30000000) {
                    continue;
                }
                
                double spawnY = player.getServerWorld().getTopY(Heightmap.Type.WORLD_SURFACE, (int) spawnX, (int) spawnZ);
                
                // Check Y bounds
                if (spawnY < player.getServerWorld().getBottomY() || spawnY > player.getServerWorld().getTopY()) {
                    continue;
                }
                
                return new Vec3d(spawnX, spawnY + 1, spawnZ);
                
            } catch (Exception e) {
                CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "Spawn position attempt " + (attempt + 1) + " failed: " + e.getMessage());
            }
        }
        
        // Fallback position
        return new Vec3d(playerPos.x + 50, playerPos.y + 10, playerPos.z + 50);
    }
} 