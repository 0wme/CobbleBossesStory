# Guide de Debug - Problème Items

## 🐛 Problème Identifié
Les commandes fonctionnent et donnent des items, mais les récompenses de type `"item"` ne donnent rien.

## 🔧 Corrections Apportées

J'ai amélioré la méthode qui donne les items avec **deux approches** :

### Approche 1: Inventaire Direct
- Essaie de donner l'item directement dans l'inventaire du joueur
- Plus fiable et plus rapide
- Si l'inventaire est plein, l'item est droppé au sol

### Approche 2: Commande Give (Fallback)  
- Si l'approche 1 échoue, utilise une commande `give` simple
- Pas de NBT compliqués pour éviter les erreurs de syntaxe

## 🧪 Comment Tester

### 1. Activer le Debug
Dans `/config/cobblebosses/config.json` :
```json
{
  "debug": true,
  "dropRolls": 1
}
```

### 2. Configuration de Test Simple
Créez un boss avec des items basiques :
```json
{
  "id": "test_boss",
  "rewards": {
    "rewards": [
      {
        "type": "item",
        "name": "Diamant Test",
        "identifier": "minecraft:diamond",
        "quantityMin": 1,
        "quantityMax": 1,
        "weight": 5.0,
        "customModelData": -1,
        "value": ""
      },
      {
        "type": "command",
        "name": "Test Commande",
        "value": "give {player} minecraft:emerald 1",
        "weight": 5.0,
        "customModelData": -1,
        "identifier": ""
      }
    ]
  }
}
```

### 3. Observer les Logs
Avec `debug: true`, vous devriez voir :
```
[CobbleBosses] Giving 1 reward(s) to PlayerName
[CobbleBosses] Roll 1/1: Diamant Test
[CobbleBosses] Gave item via inventory: minecraft:diamond x1 to PlayerName
```

OU si ça échoue :
```
[CobbleBosses] Failed to give item via inventory, trying command: [erreur]
[CobbleBosses] Executing command: give PlayerName minecraft:diamond 1
```

## 🔍 Diagnostics Possibles

### Si aucun log n'apparaît :
- Le boss n'est pas correctement configuré
- Les récompenses ne sont pas valides
- Le système de combat ne fonctionne pas

### Si vous voyez "Failed to give item via inventory" :
- L'identifier de l'item est incorrect
- L'item n'existe pas dans le jeu
- Problème avec le registre des items

### Si vous voyez "Executing command" mais pas d'item :
- La commande give échoue silencieusement
- Problème de permissions du serveur
- Problème avec le nom du joueur

## ⚡ Tests Rapides

### Test 1: Items Vanilla
```json
"identifier": "minecraft:diamond"
"identifier": "minecraft:iron_ingot"  
"identifier": "minecraft:gold_ingot"
```

### Test 2: Items Cobblemon
```json
"identifier": "cobblemon:poke_ball"
"identifier": "cobblemon:great_ball"
```

### Test 3: Items Modded
```json
"identifier": "myths_and_legends:adamant_orb"
```

## 🚀 Nouveau .jar à Tester

Le nouveau fichier .jar contient ces corrections :
- **Fabric**: `fabric/build/libs/1.21.1/1.0.3/0b3433d/CobbleBosses-1.21.1-fabric-1.0.3-0b3433d.jar`
- **NeoForge**: `neoforge/build/libs/1.21.1/1.0.3/0b3433d/CobbleBosses-1.21.1-neoforge-1.0.3-0b3433d.jar`

## 📋 Checklist de Test

- [ ] Installer le nouveau .jar
- [ ] Activer `debug: true`
- [ ] Tester avec un item minecraft:diamond simple
- [ ] Vérifier les logs du serveur
- [ ] Comparer avec une commande qui fonctionne
- [ ] Rapporter les logs si ça ne fonctionne toujours pas

## 💡 Si Ça Ne Fonctionne Toujours Pas

Envoyez-moi :
1. Les logs du serveur avec `debug: true`
2. Votre configuration exacte du boss
3. Le message d'erreur (s'il y en a)

Je pourrai alors identifier le problème exact et le corriger ! 