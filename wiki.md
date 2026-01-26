# KubeJS Integration - Void Miners

## Recipe Syntax

```javascript
event.recipes.voidminers.miner(item, dimension, minTier)
    .weight(value)
    .count(value)
    .allowHigherTiers(boolean);
```

---

## Basic Example

```javascript
event.recipes.voidminers.miner('minecraft:sand', 'minecraft:overworld', 1)
    .weight(10.0)
    .count(2)
    .allowHigherTiers(true);
```

Creates a recipe that mines sand in the Overworld, requires tier 1 miner, has a weight of 10.0, drops 2 items, and allows higher tier miners to use this recipe.

---

## Parameters

### Required Parameters (Positional)

| Parameter | Type | Description |
|-----------|------|-------------|
| `item` | String | The item to be mined (format: `'mod:item_id'`) |
| `dimension` | String | The dimension where this recipe works (e.g., `'minecraft:overworld'`) |
| `minTier` | Integer | Minimum tier of the miner required to use this recipe |

### Optional Methods (Chainable)

| Method | Type | Default | Description |
|--------|------|---------|-------------|
| `.weight(value)` | Double | `1.0` | Probability weight - higher values make the item more common |
| `.count(value)` | Integer | `1` | Amount of items dropped per mining operation |
| `.allowHigherTiers(boolean)` | Boolean | `true` | Whether higher tier miners can use this recipe |

---

## Examples

### Simple Recipe (Only Required Parameters)

```javascript
event.recipes.voidminers.miner('minecraft:coal', 'minecraft:overworld', 1);
```

Mines coal in the Overworld with default values (weight: 1.0, count: 1, allowHigherTiers: true).

---

### Common Resource (High Weight)

```javascript
event.recipes.voidminers.miner('minecraft:cobblestone', 'minecraft:overworld', 1)
    .weight(50.0)
    .count(16);
```

Very common cobblestone mining with high weight and 16 items per drop.

---

### Rare Resource (Low Weight)

```javascript
event.recipes.voidminers.miner('minecraft:diamond', 'minecraft:overworld', 2)
    .weight(2.0)
    .count(1);
```

Rare diamond mining requiring tier 2 miner with low weight.

---

### Tier-Specific Recipe

```javascript
event.recipes.voidminers.miner('minecraft:netherite_ingot', 'minecraft:the_nether', 4)
    .weight(1.0)
    .count(1)
    .allowHigherTiers(false);
```

Netherite mining ONLY available for tier 4 miners (higher tiers cannot use this recipe).

---

### The Nether Recipes

```javascript
event.recipes.voidminers.miner('minecraft:netherrack', 'minecraft:the_nether', 1)
    .weight(30.0)
    .count(8);
```

Common netherrack mining in the Nether.

```javascript
event.recipes.voidminers.miner('minecraft:ancient_debris', 'minecraft:the_nether', 3)
    .weight(0.5)
    .count(1);
```

Very rare ancient debris requiring tier 3 miner.

---

### The End Recipes

```javascript
event.recipes.voidminers.miner('minecraft:end_stone', 'minecraft:the_end', 2)
    .weight(25.0)
    .count(12);
```

Common end stone mining in the End dimension.

```javascript
event.recipes.voidminers.miner('minecraft:shulker_shell', 'minecraft:the_end', 3)
    .weight(1.5)
    .count(1);
```

Rare shulker shell mining requiring tier 3 miner.

---

### Modded Items

```javascript
event.recipes.voidminers.miner('create:zinc_ingot', 'minecraft:overworld', 2)
    .weight(8.0)
    .count(3);
```

Mining modded items from Create mod.

```javascript
event.recipes.voidminers.miner('thermal:silver_ingot', 'minecraft:overworld', 2)
    .weight(6.0)
    .count(2);
```

Mining silver from Thermal series.

---

### Custom Dimensions

```javascript
event.recipes.voidminers.miner('minecraft:glowstone_dust', 'twilightforest:twilight_forest', 2)
    .weight(15.0)
    .count(4);
```

Mining in custom dimensions from other mods.

---

## Removing Recipes

### Remove All Void Miner Recipes

```javascript
ServerEvents.recipes(event => {
    event.remove({ type: 'voidminers:miner' });
});
```

---

### Remove Specific Item Recipes

```javascript
ServerEvents.recipes(event => {
    event.remove({ 
        type: 'voidminers:miner',
        item: 'minecraft:diamond'
    });
});
```

---

### Remove By Dimension

```javascript
ServerEvents.recipes(event => {
    event.remove({ 
        type: 'voidminers:miner',
        dimension: 'minecraft:the_nether'
    });
});
```

---

## Complete Example Script

```javascript
// kubejs/server_scripts/void_miners.js

ServerEvents.recipes(event => {
    
    // Remove all default recipes
    event.remove({ type: 'voidminers:miner' });
    
    // Overworld - Tier 1
    event.recipes.voidminers.miner('minecraft:cobblestone', 'minecraft:overworld', 1)
        .weight(40.0)
        .count(16);
    
    event.recipes.voidminers.miner('minecraft:coal', 'minecraft:overworld', 1)
        .weight(20.0)
        .count(4);
    
    event.recipes.voidminers.miner('minecraft:iron_ore', 'minecraft:overworld', 1)
        .weight(15.0)
        .count(2);
    
    // Overworld - Tier 2
    event.recipes.voidminers.miner('minecraft:gold_ore', 'minecraft:overworld', 2)
        .weight(10.0)
        .count(2);
    
    event.recipes.voidminers.miner('minecraft:redstone', 'minecraft:overworld', 2)
        .weight(12.0)
        .count(8);
    
    event.recipes.voidminers.miner('minecraft:lapis_lazuli', 'minecraft:overworld', 2)
        .weight(8.0)
        .count(6);
    
    // Overworld - Tier 3
    event.recipes.voidminers.miner('minecraft:diamond', 'minecraft:overworld', 3)
        .weight(3.0)
        .count(1);
    
    event.recipes.voidminers.miner('minecraft:emerald', 'minecraft:overworld', 3)
        .weight(2.0)
        .count(1);
    
    // Nether - Tier 2
    event.recipes.voidminers.miner('minecraft:netherrack', 'minecraft:the_nether', 2)
        .weight(35.0)
        .count(12);
    
    event.recipes.voidminers.miner('minecraft:quartz', 'minecraft:the_nether', 2)
        .weight(15.0)
        .count(4);
    
    event.recipes.voidminers.miner('minecraft:glowstone_dust', 'minecraft:the_nether', 2)
        .weight(10.0)
        .count(8);
    
    // Nether - Tier 3
    event.recipes.voidminers.miner('minecraft:ancient_debris', 'minecraft:the_nether', 3)
        .weight(0.8)
        .count(1);
    
    // End - Tier 3
    event.recipes.voidminers.miner('minecraft:end_stone', 'minecraft:the_end', 3)
        .weight(30.0)
        .count(16);
    
    event.recipes.voidminers.miner('minecraft:chorus_fruit', 'minecraft:the_end', 3)
        .weight(8.0)
        .count(4);
    
    event.recipes.voidminers.miner('minecraft:shulker_shell', 'minecraft:the_end', 3)
        .weight(1.0)
        .count(1);
});
```

---

## Tips & Best Practices

1. **Weight Balancing**: Higher weights mean more common drops. Balance weights relative to each other within the same dimension/tier.

2. **Tier Progression**: Use higher tiers for more valuable resources to create progression.

3. **Count Amounts**: Set higher counts for common blocks (cobblestone, dirt) and lower counts for valuable items (diamonds, netherite).

4. **Dimension-Specific Resources**: Keep dimension-specific items (netherrack, end stone) in their respective dimensions for logical gameplay.

5. **allowHigherTiers**: Set to `false` only for recipes you want to lock to specific tiers (useful for balance).

---

## Common Dimensions

- `'minecraft:overworld'` - The Overworld
- `'minecraft:the_nether'` - The Nether
- `'minecraft:the_end'` - The End
- Custom dimensions from mods (e.g., `'twilightforest:twilight_forest'`, `'the_bumblezone:the_bumblezone'`)