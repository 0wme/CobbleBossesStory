package com.kingpixel.cobblebosses.config;

import com.google.gson.Gson;
import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class Config {
  private boolean debug;
  private String prefix;
  private String lang;
  private List<String> commands;
  private int rateSpawn;
  private List<String> blackListWorlds;
  private int dropRolls;
  
  // Auto spawning system
  private int spawnInterval;
  private String customAspect;
  private boolean broadcastSpawns;
  private String broadcastMessage;
  
  // Boss spawn chance
  private double bossSpawnChance;

  public Config() {
    debug = false;
    prefix = "§7[§6CobbleBosses§7] ";
    lang = "fr";
    commands = List.of("cobblebosses", "bosses");
    rateSpawn = 2048;
    blackListWorlds = List.of("minecraft:world_nether", "minecraft:world_the_end");
    dropRolls = 2;
    spawnInterval = 20;
    customAspect = "";
    broadcastSpawns = true;
    broadcastMessage = "<white>🔥 </white>Un <red><b>{boss}</b> <white>est apparu dans le biome <red><b>{biome}</b> <white>près de <white><b>{player}</b><white>!";
    bossSpawnChance = 0.3;
  }

  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(CobbleBosses.PATH, "config.json",
      el -> {
        Gson gson = Utils.newGson();
        CobbleBosses.config = gson.fromJson(el, Config.class);
        String data = gson.toJson(CobbleBosses.config);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleBosses.PATH, "config.json",
          data);
        if (!futureWrite.join()) {
          CobbleUtils.LOGGER.fatal(CobbleBosses.MOD_ID, "Could not write config.json file for " + CobbleBosses.MOD_NAME +
            ".");
        }
      });

    if (!futureRead.join()) {
      CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "No config.json file found for " + CobbleBosses.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleBosses.PATH, "config.json",
        data);

      if (!futureWrite.join()) {
        CobbleUtils.LOGGER.fatal(CobbleBosses.MOD_ID, "Could not write config.json file for " + CobbleBosses.MOD_NAME +
          ".");
      }
    }
  }
}