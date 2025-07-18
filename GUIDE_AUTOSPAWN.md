# 🚀 Guide du Système de Spawn Automatique CobbleBosses

## ✅ Nouveau Système Implémenté !

J'ai implémenté avec succès le **système complet de spawn automatique** que vous avez demandé ! Voici tout ce qui a été ajouté :

## 🆕 Fonctionnalités Ajoutées

### 1. Configuration Complète
Tous les paramètres de votre config sont maintenant **fonctionnels** :

```json
{
  "spawnInterval": 60,              // Spawn un boss toutes les 60 secondes
  "customAspect": "",               // Aspect personnalisé pour les Pokémon
  "broadcastSpawns": true,          // Activer les annonces de spawn
  "broadcastMessage": "<white>🔥 </white>Un <red><b>{boss}</b> <white>est apparu dans le biome <red><b>{biome}</b> <white>près de <white><b>{player}</b><white>!",
  "bossSpawnChance": 0.3,           // 30% de chance de spawn à chaque intervalle
  "commonWeight": 50,               // Poids pour la sélection aléatoire
  "commonBaseLevel": 10,            // Niveau minimum des boss communs
  "commonOverLevel": 30             // Niveau maximum des boss communs
}
```

### 2. Système AutoSpawner
- ⏰ **Timer automatique** basé sur `spawnInterval`
- 🎯 **Spawn près d'un joueur aléatoire** (16-64 blocs)
- 🌍 **Respect de la blacklist des mondes**
- 🎲 **Système de chance configurable**
- 📢 **Messages de broadcast avec placeholders**

### 3. Placeholders Supportés
Dans `broadcastMessage` :
- `{boss}` - Nom du boss spawné
- `{biome}` - Nom du biome (formaté joliment)
- `{player}` - Nom du joueur le plus proche
- `%boss%`, `%biome%`, `%player%` - Versions alternatives

### 4. Commandes de Contrôle
Nouvelles commandes `/cobblebosses autospawn` :
- `/cobblebosses autospawn start` - Démarrer l'auto spawn
- `/cobblebosses autospawn stop` - Arrêter l'auto spawn  
- `/cobblebosses autospawn restart` - Redémarrer l'auto spawn
- `/cobblebosses autospawn status` - Voir le statut actuel

## 🔧 Configuration Recommandée

### Pour Tester Rapidement
```json
{
  "debug": true,
  "spawnInterval": 30,           // Toutes les 30 secondes
  "bossSpawnChance": 1.0,        // 100% de chance (pour test)
  "broadcastSpawns": true,
  "broadcastMessage": "<white>榕 </white>Un <red><b>{boss}</b> <white>est apparu dans le biome <red><b>{biome}</b> <white>près de <white><b>{player}</b><white>!"
}
```

### Pour Production
```json
{
  "debug": false,
  "spawnInterval": 300,          // Toutes les 5 minutes (300 secondes)
  "bossSpawnChance": 0.3,        // 30% de chance
  "broadcastSpawns": true
}
```

## 🎮 Comment Utiliser

### 1. Installation
Remplacez votre ancien `.jar` par le nouveau compilé avec les fonctionnalités.

### 2. Configuration
Copiez le contenu d'`EXEMPLE_CONFIG_AUTOSPAWN.json` dans votre `/config/cobblebosses/config.json`

### 3. Redémarrage
- Démarrez votre serveur OU
- Utilisez `/cobblebosses reload` pour recharger la config

### 4. Vérification
- `/cobblebosses autospawn status` pour voir si c'est actif
- Avec `"debug": true`, vous verrez les logs dans la console

## 📊 Fonctionnement Détaillé

### Algorithme de Spawn
1. **Timer** : Toutes les X secondes (`spawnInterval`)
2. **Vérification** : Y a-t-il des joueurs en ligne ?
3. **Sélection** : Choisir un joueur aléatoire
4. **Monde** : Vérifier que le monde n'est pas blacklisté
5. **Chance** : Tester `bossSpawnChance` (ex: 0.3 = 30%)
6. **Position** : Calculer un point dans un rayon de 16-64 blocs
7. **Boss** : Sélectionner un boss selon les poids configurés
8. **Spawn** : Créer le boss et broadcast le message

### Système de Poids
Les boss sont sélectionnés selon leur rareté :
- **Common** : 50% de chances (weight: 50)
- **Uncommon** : 30% de chances (weight: 30)  
- **Rare** : 15% de chances (weight: 15)
- **Legendary** : 5% de chances (weight: 5)

### Gestion des Biomes
Le système détecte automatiquement le biome de spawn et l'affiche dans le message :
- `minecraft:plains` → `Plains`
- `minecraft:dark_forest` → `Dark forest`

## 🐛 Debug et Logs

Avec `"debug": true`, vous verrez dans la console :
```
[CobbleBosses] Starting auto spawning every 60 seconds (1200 ticks)
[CobbleBosses] Auto spawned boss 'common' near player 'VotreNom' at 123, 64, 456
[CobbleBosses] Spawn chance failed: 0.85 > 0.3
[CobbleBosses] Broadcasted spawn message: 🔥 Un Pikachu Boss est apparu dans le biome Plains près de VotreNom!
```

## ⚠️ Notes Importantes

### Différence avec l'Ancien Système
- **Avant** : Spawn seulement quand un Pokémon spawn naturellement (1/2048 chance)
- **Maintenant** : Timer automatique + système de spawn actif

### Performance
- Le système utilise des **tâches asynchrones** optimisées
- **Arrêt automatique** quand le serveur s'éteint
- **Redémarrage automatique** lors du reload

### Compatibilité
- ✅ Compatible avec le système de récompenses unifié
- ✅ Compatible avec tous vos boss existants
- ✅ Compatible avec les commandes de spawn manuel

## 🎯 Exemples de Messages de Broadcast

```
🔥 Un Charizard Boss est apparu dans le biome Desert près de Alice!
🔥 Un Mewtwo Boss est apparu dans le biome Mountain près de Bob!
🔥 Un Pikachu Boss est apparu dans le biome Forest près de Charlie!
```

## 🔄 Migration depuis l'Ancien Système

Si vous aviez une config comme :
```json
{
  "spawnInterval": 1,
  "broadcastSpawns": true,
  "broadcastMessage": "Un boss est apparu!"
}
```

Elle fonctionne maintenant **directement** ! Plus besoin de modifications.

---

## 🎉 Résultat Final

**Votre problème initial** : "*spawnInterval de 1 minute ne fait rien*"  
**✅ RÉSOLU** : Le système spawn maintenant **automatiquement** des boss selon l'intervalle configuré !

Testez avec `"spawnInterval": 30` et `"debug": true` pour voir les résultats immédiats ! 🚀 