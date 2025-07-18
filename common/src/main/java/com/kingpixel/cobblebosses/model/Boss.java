package com.kingpixel.cobblebosses.model;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.AdvancedItemChance;
import com.kingpixel.cobbleutils.util.Utils;
import kotlin.Unit;
import lombok.Data;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 14/02/2025 4:15
 */
@Data
public class Boss {
  private String id;
  private String nickName;
  private boolean glowing;
  private Formatting glowingColor;
  private boolean particles;
  private String particleColor;
  private float chance;
  private int maxLevel;
  private int minLevel;
  private float maxSize;
  private float minSize;
  private String properties;
  private BossRewards rewards;

  public Boss() {
    id = "default";
    nickName = "§e%pokemon% §9Boss";
    glowing = true;
    glowingColor = Formatting.LIGHT_PURPLE;
    particles = true;
    particleColor = "#CBC3E3";
    chance = 0.1f;
    maxLevel = 120;
    minLevel = 100;
    maxSize = 2.0f;
    minSize = 1.5f;
    properties = "shiny=true";
    rewards = new BossRewards();
    
    List<Reward> defaultRewards = new ArrayList<>();
    
    Reward defaultItemReward = new Reward();
    defaultItemReward.setType("item");
    defaultItemReward.setName("Default Item");
    defaultItemReward.setIdentifier("minecraft:diamond");
    defaultItemReward.setQuantityMin(1);
    defaultItemReward.setQuantityMax(3);
    defaultItemReward.setWeight(50.0);
    defaultRewards.add(defaultItemReward);
    
    Reward defaultCommandReward = new Reward();
    defaultCommandReward.setType("command");
    defaultCommandReward.setName("Default Command");
    defaultCommandReward.setValue("give %player% minecraft:gold_ingot 2");
    defaultCommandReward.setWeight(30.0);
    defaultRewards.add(defaultCommandReward);
    
    this.rewards.setRewards(defaultRewards);
  }
  
  private void createDefaultRewards() {
    Reward itemReward = new Reward();
    itemReward.setType("item");
    itemReward.setName("§6Diamant Boss");
    itemReward.setIdentifier("minecraft:diamond");
    itemReward.setQuantityMin(1);
    itemReward.setQuantityMax(3);
    itemReward.setWeight(3.0);
    
    Reward commandReward = new Reward();
    commandReward.setType("command");
    commandReward.setName("Annonce de victoire");
    commandReward.setValue("broadcast §6{player} §7a vaincu un §9Boss §7Pokémon!");
    commandReward.setWeight(1.0);
    
    rewards.getRewards().add(itemReward);
    rewards.getRewards().add(commandReward);
  }

  public void check() {
    if (minLevel > maxLevel) {
      int temp = minLevel;
      minLevel = maxLevel;
      maxLevel = temp;
    }

    if (minSize > maxSize) {
      float temp = minSize;
      minSize = maxSize;
      maxSize = temp;
    }

    if (particleColor == null) particleColor = "#CBC3E3";
    if (glowingColor == null) glowingColor = Formatting.LIGHT_PURPLE;
    if (nickName == null) nickName = "§e%pokemon% §9Boss";
    if (properties == null) properties = "shiny=true";
    if (rewards == null) rewards = new BossRewards();
  }

  public void convert(PokemonEntity p) {
    Pokemon pokemon = p.getPokemon().clone(true, DynamicRegistryManager.EMPTY);
    ServerWorld world = (ServerWorld) p.getEntityWorld();
    Vec3d pos = p.getPos();
    p.remove(Entity.RemovalReason.DISCARDED);
    spawn(world, pos, pokemon);
    
    // Broadcast boss spawn message if enabled
    if (CobbleBosses.config.isBroadcastSpawns()) {
      try {
        String biomeName = getBiomeName(world, pos);
        String playerName = findNearestPlayerName(world, pos);
        String bossDisplayName = getBossDisplayName(pokemon);
        
        broadcastSpawnMessage(bossDisplayName, biomeName, playerName);
        
        if (CobbleBosses.config.isDebug()) {
          CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Broadcasted boss spawn: " + bossDisplayName);
        }
      } catch (Exception e) {
        CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "Failed to broadcast boss spawn: " + e.getMessage());
      }
    }
  }

  private String getBiomeName(ServerWorld world, Vec3d pos) {
    try {
      BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
      var biomeEntry = world.getBiome(blockPos);
      var biomeRegistry = world.getRegistryManager().get(RegistryKeys.BIOME);
      String biomeName = biomeRegistry.getId(biomeEntry.value()).toString()
          .replace("minecraft:", "")
          .replace("_", " ");
      return biomeName.substring(0, 1).toUpperCase() + biomeName.substring(1);
    } catch (Exception e) {
      return "Unknown";
    }
  }

  private String findNearestPlayerName(ServerWorld world, Vec3d pos) {
    try {
      var nearestPlayer = world.getClosestPlayer(pos.x, pos.y, pos.z, 100.0, false);
      return nearestPlayer != null ? nearestPlayer.getName().getString() : "Un joueur";
    } catch (Exception e) {
      return "Un joueur";
    }
  }

  private String getBossDisplayName(Pokemon pokemon) {
    try {
      String pokemonName = pokemon.getDisplayName().getString();
      String rarity = getRarityName();
      return pokemonName + " (" + rarity + ")";
    } catch (Exception e) {
      return "Boss Pokemon (" + getRarityName() + ")";
    }
  }

  private String getRarityName() {
    if (chance >= 0.30) return "Commun";
    else if (chance >= 0.20) return "Uncommon";
    else if (chance >= 0.10) return "Rare";
    else return "Légendaire";
  }

  private void broadcastSpawnMessage(String bossName, String biomeName, String playerName) {
    try {
      if (CobbleBosses.server == null) return;
      
      String message = CobbleBosses.config.getBroadcastMessage()
          .replace("{boss}", bossName)
          .replace("{biome}", biomeName)
          .replace("{player}", playerName);
      
      message = convertColorTags(message);
      
      Text broadcastText = Text.literal(message);
      
      for (var player : CobbleBosses.server.getPlayerManager().getPlayerList()) {
        player.sendMessage(broadcastText, false);
      }
      
      if (CobbleBosses.config.isDebug()) {
        CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Sent broadcast message: " + message);
      }
      
    } catch (Exception e) {
      CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "Error broadcasting spawn message: " + e.getMessage());
    }
  }

  private String convertColorTags(String message) {
    return message
        .replace("<red>", "§c").replace("</red>", "§r")
        .replace("<green>", "§a").replace("</green>", "§r")
        .replace("<blue>", "§9").replace("</blue>", "§r")
        .replace("<yellow>", "§e").replace("</yellow>", "§r")
        .replace("<purple>", "§d").replace("</purple>", "§r")
        .replace("<aqua>", "§b").replace("</aqua>", "§r")
        .replace("<white>", "§f").replace("</white>", "§r")
        .replace("<black>", "§0").replace("</black>", "§r")
        .replace("<gray>", "§7").replace("</gray>", "§r")
        .replace("<dark_red>", "§4").replace("</dark_red>", "§r")
        .replace("<dark_green>", "§2").replace("</dark_green>", "§r")
        .replace("<dark_blue>", "§1").replace("</dark_blue>", "§r")
        .replace("<gold>", "§6").replace("</gold>", "§r")
        .replace("<dark_purple>", "§5").replace("</dark_purple>", "§r")
        .replace("<dark_aqua>", "§3").replace("</dark_aqua>", "§r")
        .replace("<dark_gray>", "§8").replace("</dark_gray>", "§r")
        .replace("<bold>", "§l").replace("</bold>", "§r")
        .replace("<italic>", "§o").replace("</italic>", "§r")
        .replace("<underline>", "§n").replace("</underline>", "§r")
        .replace("<strikethrough>", "§m").replace("</strikethrough>", "§r")
        .replace("<b>", "§l").replace("</b>", "§r");
  }

  private void assignBossToTeam(ServerWorld world, LivingEntity bossEntity) {
    if (bossEntity == null || world == null) {
      return;
    }
    Scoreboard scoreboard = world.getScoreboard();
    String teamColorName = "boss_" + this.glowingColor.getName();
    Team team = scoreboard.getTeam(teamColorName);

    if (team == null) {
      team = scoreboard.addTeam(teamColorName);
      team.setDisplayName(Text.literal("boss_" + this.glowingColor.getName()));
      team.setFriendlyFireAllowed(false);
    }
    team.setColor(this.glowingColor);
    scoreboard.addScoreHolderToTeam(bossEntity.getNameForScoreboard(), team);
  }

  public static void removeBossFromTeam(ServerWorld world, LivingEntity bossEntity, String teamColorName) {
    if (bossEntity == null || world == null || teamColorName == null) {
      return;
    }
    
    try {
      Scoreboard scoreboard = world.getScoreboard();
      Team team = scoreboard.getTeam(teamColorName);
      
      if (team != null && scoreboard.getScoreHolderTeam(bossEntity.getNameForScoreboard()) == team) {
        scoreboard.removeScoreHolderFromTeam(bossEntity.getNameForScoreboard(), team);
      }
    } catch (Exception e) {
      CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "Failed to remove boss from team: " + e.getMessage());
    }
  }

  public void removeBossFromTeam(ServerWorld world, LivingEntity bossEntity) {
    String teamColorName = "boss_" + this.glowingColor.getName();
    removeBossFromTeam(world, bossEntity, teamColorName);
  }

  public void spawn(ServerWorld world, Vec3d pos, Pokemon pokemon) {
    PokemonProperties.Companion.parse("uncatchable=true " + getProperties()).apply(pokemon);

    if (minSize == maxSize) {
      pokemon.setScaleModifier(maxSize);
    } else {
      pokemon.setScaleModifier(Utils.RANDOM.nextFloat(minSize, maxSize));
    }

    NbtCompound nbt = pokemon.getPersistentData();
    nbt.putString(CobbleBosses.TAG_BOSS_ID, id);

    Cobblemon.INSTANCE.getConfig().setMaxPokemonLevel(CobbleBosses.maxLevelCap);
    if (minLevel == maxLevel) {
      pokemon.setLevel(maxLevel);
    } else {
      pokemon.setLevel(Utils.RANDOM.nextInt(minLevel, maxLevel));
    }
    Cobblemon.INSTANCE.getConfig().setMaxPokemonLevel(CobbleBosses.oldLevelCap);

    PokemonEntity pokemonEntity = pokemon.sendOut(world, pos, null, bossEntity -> {
      if (glowing) {
        bossEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, -1, 0, false, false));
        assignBossToTeam(world, bossEntity);
      }

      if (particles && !particleColor.isEmpty()) {
        ParticleEffectManager particleEffectManager = new ParticleEffectManager(particleColor);
        particleEffectManager.spawnParticles(world, bossEntity);
      }

      var text = Text.empty().append(nickName.replace("%pokemon%", pokemon.getSpecies().getName()));
      bossEntity.setCustomNameVisible(true);
      bossEntity.getPokemon().setNickname(text);
      bossEntity.setCustomName(text);

      return Unit.INSTANCE;
    });
  }
}