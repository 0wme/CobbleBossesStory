# 🎨 Guide des Couleurs dans les Messages de Broadcast

## ✅ Problème Résolu !

**Problème** : Le message affichait les balises au lieu des couleurs  
**✅ Correction** : Système de conversion automatique des balises en codes couleur Minecraft

### **Avant :**
```
<white>🔥 </white>Un <red><b>Salamence Légendaire</b> <white>est apparu...
```

### **Maintenant :**
Le message s'affiche avec les vraies **couleurs Minecraft** ! 🎨

## 🎨 Balises de Couleur Supportées

### **Couleurs de Base**
```html
<white>Texte blanc</white>         → §f (blanc)
<red>Texte rouge</red>             → §c (rouge)
<green>Texte vert</green>          → §a (vert)
<blue>Texte bleu</blue>            → §9 (bleu)
<yellow>Texte jaune</yellow>       → §e (jaune)
<purple>Texte violet</purple>      → §d (violet)
<aqua>Texte cyan</aqua>            → §b (cyan)
<gold>Texte doré</gold>            → §6 (doré)
<gray>Texte gris</gray>            → §7 (gris)
<black>Texte noir</black>          → §0 (noir)
```

### **Couleurs Foncées**
```html
<dark_red>Rouge foncé</dark_red>           → §4
<dark_green>Vert foncé</dark_green>        → §2
<dark_blue>Bleu foncé</dark_blue>          → §1
<dark_purple>Violet foncé</dark_purple>    → §5
<light_purple>Violet clair</light_purple>  → §d
```

### **Styles de Texte**
```html
<b>Texte gras</b>                    → §l (gras)
<bold>Texte gras</bold>              → §l (gras)
<i>Texte italique</i>                → §o (italique)
<italic>Texte italique</italic>      → §o (italique)
<u>Texte souligné</u>                → §n (souligné)
<underline>Texte souligné</underline> → §n (souligné)
<s>Texte barré</s>                   → §m (barré)
<strikethrough>Texte barré</strikethrough> → §m (barré)
```

## 🚀 Exemples d'Utilisation

### **Message Standard**
```json
"broadcastMessage": "<white>🔥 </white>Un <red><b>{boss}</b></red> <white>est apparu dans le biome <green><b>{biome}</b></green> <white>près de <yellow><b>{player}</b></yellow><white>!"
```

**Résultat :**
🔥 Un **Pikachu Légendaire** est apparu dans le biome **Plains** près de **VotreNom**!

### **Message Coloré Avancé**
```json
"broadcastMessage": "<gold>⚡ BOSS SPAWN ⚡</gold> <white>Un <red><bold>{boss}</bold></red> <white>de niveau <yellow>{level}</yellow> <white>est apparu en <aqua><italic>{biome}</italic></aqua> <white>près de <green><underline>{player}</underline></green><white>!"
```

### **Message avec Émojis**
```json
"broadcastMessage": "<white>🔥⚔️ </white><red><b>ALERTE BOSS</b></red><white> ⚔️🔥</white>\n<white>Un <purple><b>{boss}</b></purple> <white>sauvage apparaît près de <gold><b>{player}</b></gold><white>!"
```

## 📋 Votre Configuration Actuelle

Votre message :
```json
"broadcastMessage": "<white>🔥 </white>Un <red><b>{boss}</b> <white>est apparu dans le biome <red><b>{biome}</b> <white>près de <white><b>{player}</b><white>!"
```

**Se convertit automatiquement en :**
- `<white>🔥 </white>` → 🔥 (blanc)
- `<red><b>{boss}</b>` → **Nom du Boss** (rouge et gras)
- `<white>est apparu dans le biome` → texte blanc
- `<red><b>{biome}</b>` → **Nom du Biome** (rouge et gras)  
- `<white>près de` → texte blanc
- `<white><b>{player}</b>` → **Nom du Joueur** (blanc et gras)

## 🎯 Exemples de Messages Finaux

Avec votre configuration, vous verrez maintenant :

```
🔥 Un Charizard Rare est apparu dans le biome Plains près de Alice!
🔥 Un Mewtwo Légendaire est apparu dans le biome Forest près de Bob!
🔥 Un Pikachu Commun est apparu dans le biome Mountain près de Charlie!
```

Avec les **vraies couleurs** :
- 🔥 en blanc
- **Charizard Rare** en rouge gras
- **Plains** en rouge gras  
- **Alice** en blanc gras

## 🔧 Conseils de Configuration

### **Pour un Message Épique :**
```json
"broadcastMessage": "<gold>⭐ </gold><red><bold>BOSS LÉGENDAIRE DÉTECTÉ</bold></red><gold> ⭐</gold>\n<white>Un <purple><bold>{boss}</bold></purple> <white>est apparu en <aqua><italic>{biome}</italic></aqua> <white>près de <yellow><underline>{player}</underline></yellow><white>!"
```

### **Pour un Message Simple :**
```json
"broadcastMessage": "<white>Un <red>{boss}</red> <white>est apparu près de <yellow>{player}</yellow><white>!"
```

### **Pour un Message Coloré :**
```json
"broadcastMessage": "<green>🌟 </green><white>Un <gold><b>{boss}</b></gold> <white>sauvage apparaît dans <blue><i>{biome}</i></blue> <white>près de <red><b>{player}</b></red><white>! 🌟"
```

## ⚡ Fonctionnement Automatique

Le système **convertit automatiquement** toutes les balises :
1. **Détecte** les balises HTML dans votre message
2. **Convertit** en codes couleur Minecraft (§)
3. **Affiche** le message avec les vraies couleurs
4. **Fonctionne** avec tous les placeholders (`{boss}`, `{biome}`, `{player}`)

## 🎉 Résultat

**Plus jamais** de balises affichées dans le chat !
**Maintenant** : Couleurs magnifiques et lisibles ! 🌈

Votre message va maintenant s'afficher avec de **vraies couleurs Minecraft** au lieu des balises texte ! 🚀 