# 🔥 CobbleBosses - Système Plug & Play

## ✅ **SYSTÈME 100% AUTOMATIQUE**

**Tu installes le mod → Ça marche tout seul !** 🚀

## 📦 **Installation ultra-simple**

1. **Remplace le .jar** sur ton serveur (fichier dans `/actual_versions/`)
2. **Redémarre le serveur**
3. **C'est tout !** ✨

## ⚡ **Que se passe-t-il automatiquement ?**

### 🚀 **Au démarrage du serveur :**
```
[INFO]: 🚀 CobbleBosses AutoSpawn activated! Every 20 minutes, 30.0% chance of boss spawn
[INFO]: ✅ AutoSpawn system ready! Next boss check in 20 minutes.
```

### ⏰ **Toutes les 20 minutes :**
```
[INFO]: AutoSpawn check: 25.3% vs 30.0% chance
[INFO]: AutoSpawned boss: rare near TonNom
```

### 📢 **Message dans le chat :**
```
🔥 Un Pikachu est apparu dans le biome Plains près de TonNom!
```

## 🎯 **Fonctionnement automatique**

- ⏰ **Timer** : Toutes les 20 minutes exactement
- 🎲 **Chance** : 30% de probabilité à chaque fois
- 🗺️ **Spawn** : 30-80 blocs d'un joueur aléatoire
- 📢 **Broadcast** : Message automatique dans le chat
- 🎁 **Récompenses** : 2 récompenses par boss vaincu

## 🔧 **Configuration (optionnelle)**

Si tu veux changer les paramètres, édite `config.json` :

```json
{
  "spawnInterval": 20,        // Minutes entre chaque check
  "bossSpawnChance": 0.3,     // 30% de chance (0.3 = 30%)
  "broadcastSpawns": true,    // Messages dans le chat
  "dropRolls": 2              // Nombre de récompenses
}
```

### 📝 **Exemples de personnalisation :**

**Boss plus fréquents (10min, 50%) :**
```json
"spawnInterval": 10,
"bossSpawnChance": 0.5
```

**Boss plus rares (60min, 15%) :**
```json
"spawnInterval": 60,
"bossSpawnChance": 0.15
```

**Désactiver complètement :**
```json
"spawnInterval": 0
```

## 🚫 **Aucune commande nécessaire !**

- ❌ Plus de `/cobblebosses autospawn start`
- ❌ Plus de configuration manuelle
- ✅ Juste `/cobblebosses reload` pour recharger la config

## 📊 **Statistiques**

- **Fréquence réelle** : ~1.5 boss par heure en moyenne
- **2 systèmes** : Boss programmés (20min) + Boss naturels (1/2048)
- **Zones exclues** : Nether, End automatiquement

## 🎉 **RÉSUMÉ**

**Tu mets le mod → Boss automatiques toutes les 20min (30% chance) → Messages dans le chat → Récompenses automatiques → Aucune commande à taper !**

**C'est vraiment du plug & play !** 🔌✨ 