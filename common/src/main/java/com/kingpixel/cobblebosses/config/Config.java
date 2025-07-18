package com.kingpixel.cobblebosses.config;

import com.google.gson.Gson;
import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Carlos Varas Alonso - 29/04/2024 0:14
 */
@Getter
@Data
@ToString
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
  
  // Boss type configuration
  private double bossSpawnChance;
  private boolean shinyCommonBosses;
  private boolean shinyUncommonBosses;
  private boolean shinyRareBosses;
  private boolean shinyLegendaryBosses;
  
  // Boss weights and levels
  private int commonWeight;
  private int commonBaseLevel;
  private int commonOverLevel;
  private int uncommonWeight;
  private int uncommonBaseLevel;
  private int uncommonOverLevel;
  private int rareWeight;
  private int rareBaseLevel;
  private int rareOverLevel;
  private int legendaryWeight;
  private int legendaryBaseLevel;
  private int legendaryOverLevel;
  
  // Size modifiers
  private double commonSizeModifier;
  private double uncommonSizeModifier;
  private double rareSizeModifier;
  private double legendarySizeModifier;
  
  // Boss abilities
  private List<String> bossAbilities;

  public Config() {
    debug = false;
    prefix = "§7[§6CobbleBosses§7] ";
    lang = "en";
    commands = List.of("cobblebosses", "bosses");
    rateSpawn = 2048;
    blackListWorlds = List.of("minecraft:world_nether", "minecraft:world_the_end");
    dropRolls = 1;
    
    // Auto spawning defaults
    spawnInterval = 300; // 5 minutes in seconds
    customAspect = "";
    broadcastSpawns = true;
    broadcastMessage = "<white>🔥 </white>Un <red><b>{boss}</b> <white>est apparu dans le biome <red><b>{biome}</b> <white>près de <white><b>{player}</b><white>!";
    
    // Boss type defaults
    bossSpawnChance = 0.3;
    shinyCommonBosses = true;
    shinyUncommonBosses = true;
    shinyRareBosses = true;
    shinyLegendaryBosses = true;
    
    // Weights and levels defaults (30% Common, 40% Uncommon, 20% Rare, 10% Legendary)
    commonWeight = 30;
    commonBaseLevel = 10;
    commonOverLevel = 30;
    uncommonWeight = 40;
    uncommonBaseLevel = 30;
    uncommonOverLevel = 50;
    rareWeight = 20;
    rareBaseLevel = 90;
    rareOverLevel = 100;
    legendaryWeight = 10;
    legendaryBaseLevel = 100;
    legendaryOverLevel = 150;
    
    // Size modifiers defaults
    commonSizeModifier = 5.0;
    uncommonSizeModifier = 5.0;
    rareSizeModifier = 5.0;
    legendarySizeModifier = 5.0;
    
    // Boss abilities defaults
    bossAbilities = List.of("unaware", "magicbounce", "magicguard", "goodasgold");
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
      CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "No config.json file found for" + CobbleBosses.MOD_NAME + ". Attempting" +
        " to generate one.");
      Gson gson = Utils.newGson();
      CobbleBosses.config = this;
      String data = gson.toJson(CobbleBosses.config);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleBosses.PATH, "config.json",
        data);

      if (!futureWrite.join()) {
        CobbleUtils.LOGGER.fatal(CobbleBosses.MOD_ID, "Could not write config.json file for " + CobbleBosses.MOD_NAME + ".");
      }
    }

  }
}