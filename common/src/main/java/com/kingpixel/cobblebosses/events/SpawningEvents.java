package com.kingpixel.cobblebosses.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobblebosses.config.BossesConfig;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import com.kingpixel.cobbleutils.util.Utils;

import kotlin.Unit;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

/**
 * @author Carlos Varas Alonso - 14/02/2025 4:18
 */
public class SpawningEvents {

  public static void register() {
    CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.HIGHEST, evt -> {
      try {
        var pokemonEntity = evt.getEntity();
        var pokemon = pokemonEntity.getPokemon();
        
        if (CobbleBosses.config.isDebug()) {
          CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Pokemon spawn detected: " + pokemon.getDisplayName().getString());
        }
        
        if (isSpecial(pokemon)) {
          if (CobbleBosses.config.isDebug()) {
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Pokemon is special, skipping boss conversion");
          }
          return Unit.INSTANCE;
        }
        
        ServerWorld world = (ServerWorld) pokemonEntity.getEntityWorld();
        String s = world.getRegistryKey().getValue().toString();
        if (CobbleBosses.config.getBlackListWorlds().contains(s)) {
          if (CobbleBosses.config.isDebug()) {
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "World is blacklisted: " + s);
          }
          return Unit.INSTANCE;
        }
        
        int rateSpawn = CobbleBosses.config.getRateSpawn();
        int random = Utils.RANDOM.nextInt(rateSpawn);
        
        if (CobbleBosses.config.isDebug()) {
          CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Boss spawn chance: " + random + "/" + rateSpawn + " (need 0)");
        }
        
        if (random == 0) {
          var boss = BossesConfig.getRandomBoss();
          if (boss == null) {
            CobbleUtils.LOGGER.warn(CobbleBosses.MOD_ID, "No boss available for conversion! Check boss configs.");
            return Unit.INSTANCE;
          }
          
          if (CobbleBosses.config.isDebug()) {
            CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Converting pokemon to boss: " + boss.getId());
          }
          
          boss.convert(pokemonEntity);
          evt.cancel();
        }
      } catch (Exception e) {
        CobbleUtils.LOGGER.error(CobbleBosses.MOD_ID, "Error in boss spawning: " + e.getMessage());
        e.printStackTrace();
      }
      return Unit.INSTANCE;
    });
  }

  private static boolean isSpecial(Pokemon p) {
    NbtCompound nbt = p.getPersistentData();
    boolean isSpecial = PokemonUtils.getIvsAverage(p.getIvs()) >= 31 || p.isLegendary() || p.getShiny() || nbt.contains(CobbleBosses.TAG_BOSS_ID);
    
    if (CobbleBosses.config.isDebug() && isSpecial) {
      CobbleUtils.LOGGER.info(CobbleBosses.MOD_ID, "Pokemon is special - IVs: " + PokemonUtils.getIvsAverage(p.getIvs()) + 
        ", Legendary: " + p.isLegendary() + ", Shiny: " + p.getShiny() + ", Already Boss: " + nbt.contains(CobbleBosses.TAG_BOSS_ID));
    }
    
    return isSpecial;
  }
}
