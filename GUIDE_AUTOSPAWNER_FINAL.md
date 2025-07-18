# 🎯 Guide AutoSpawner - Système Programmé

## ✅ **SYSTÈME IMPLÉMENTÉ**

L'AutoSpawner a été **réimplémenté** selon tes spécifications :  
**"Toutes les 20 minutes, 30% de chance qu'un boss spawn"**

## 📋 **Configuration**

### 🔧 **Paramètres clés du config.json**

```json
{
  "spawnInterval": 20,        // ⏱️ Intervalle en MINUTES (20 = toutes les 20min)
  "bossSpawnChance": 0.3,     // 🎲 30% de chance (0.3 = 30%)
  "rateSpawn": 2048,          // 🔀 Boss naturels (1/2048 chance Pokémon → Boss)
  "broadcastSpawns": true,    // 📢 Messages de broadcast activés
  "dropRolls": 2              // 🎁 Nombre de récompenses par boss
}
```

### 🆚 **Différence entre les systèmes**

| Paramètre | Usage | Description |
|-----------|--------|-------------|
| `spawnInterval` + `bossSpawnChance` | **AutoSpawner** | Boss programmés (timer fixe) |
| `rateSpawn` | **Spawn naturel** | Boss aléatoires (quand Pokémon spawn) |

## 🎮 **Commandes disponibles**

```
/cobblebosses autospawn start    # Démarrer l'AutoSpawner
/cobblebosses autospawn stop     # Arrêter l'AutoSpawner
/cobblebosses autospawn restart  # Redémarrer l'AutoSpawner
/cobblebosses autospawn status   # Voir le statut
/cobblebosses reload             # Recharger config + restart AutoSpawner
```

## 🔄 **Fonctionnement**

### ⏰ **Timeline typique**
```
00:00 - Serveur démarre → AutoSpawner démarre automatiquement
00:20 - 1er check → 30% chance → Boss spawn ✅ ou non ❌
00:40 - 2ème check → 30% chance → Boss spawn ✅ ou non ❌
01:00 - 3ème check → 30% chance → Boss spawn ✅ ou non ❌
...
```

### 🎯 **Logique de spawn**
1. **Timer** : Toutes les 20 minutes exactement
2. **Chance** : Génère random 0-100%, compare avec 30%
3. **Joueur** : Sélectionne un joueur en ligne aléatoirement
4. **Position** : Trouve position sûre 30-80 blocs du joueur
5. **Boss** : Sélectionne boss selon poids (30% Common, 40% Uncommon, 20% Rare, 10% Légendaire)
6. **Broadcast** : Message dans le chat si activé

### 📊 **Statistiques**
- **Probabilité réelle** : ~30% toutes les 20min = ~1.5 boss/heure en moyenne
- **Zone de spawn** : 30-80 blocs autour d'un joueur aléatoire
- **Mondes exclus** : Nether, End (configurable)

## 🚀 **Installation/Test**

1. **Remplace le .jar** avec le nouveau dans `/actual_versions/`
2. **Copie le config.json** mis à jour
3. **Redémarre le serveur**
4. **Vérifie les logs** :
   ```
   [INFO]: Starting AutoSpawner: Every 20 minutes, 30.0% chance
   [INFO]: AutoSpawner started successfully
   ```

## 🔧 **Personnalisation**

### 📝 **Exemples de configurations**

**Boss fréquents (toutes les 5min, 50% chance)**
```json
"spawnInterval": 5,
"bossSpawnChance": 0.5
```

**Boss rares (toutes les 60min, 10% chance)**
```json
"spawnInterval": 60,
"bossSpawnChance": 0.1
```

**Désactiver l'AutoSpawner**
```json
"spawnInterval": 0
```

## 🛠️ **Dépannage**

### ❌ **Si aucun boss ne spawn**
1. Vérifier `/cobblebosses autospawn status`
2. Vérifier que `spawnInterval > 0`
3. Vérifier les logs pour "AutoSpawn check"

### 📋 **Logs utiles**
```
[INFO]: AutoSpawn check: 25.3% vs 30.0% chance → Boss spawned ✅
[INFO]: AutoSpawn check: 85.7% vs 30.0% chance → No boss spawned ❌
```

---

## 🎉 **RÉSUMÉ**

**Configuration actuelle** = Toutes les 20min, 30% de chance qu'un boss spawn automatiquement + système naturel (1/2048) qui continue en parallèle.

**Commandes** = `/cobblebosses autospawn start/stop/restart/status`

**Message** = "🔥 Un [Boss] est apparu dans le biome [Biome] près de [Joueur]!" 