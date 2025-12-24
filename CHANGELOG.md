# Changelog

## 1.21.1-1.5.3
-# Changelog

## [Unreleased]

### Added
- **KubeJS Integration**: Full support for creating Void Miner recipes through KubeJS
  - Added recipe schema for easy recipe creation
  - Added builder pattern support with chainable methods
  - Recipes can now be created using `event.recipes.voidminers.miner()`
  - Support for all recipe parameters: item output, dimension, tier, weight, count, and tier restrictions
  - Optional parameters have sensible defaults (weight: 1.0, count: 1, allowHigherTiers: true)
  - KubeJS is marked as an optional dependency - mod works without it

### Changed
- Marked KubeJS as an optional dependency in mod metadata

### Technical Details
- Implemented `VoidMinerRecipeSchema` for recipe component definitions
- Implemented `VoidMinersKubeJSPlugin` for KubeJS plugin registration
- Recipe type is registered as `voidminers:miner`
- All recipe modifications are done through standard KubeJS recipe events

### Documentation
- Added comprehensive KubeJS integration wiki with examples
- Included usage examples for all recipe types and parameters
- Added tips and best practices for recipe creation

---

## Example Usage

```javascript
// Simple recipe
event.recipes.voidminers.miner('minecraft:diamond', 'minecraft:overworld', 3)
    .weight(2.0)
    .count(1);

// Advanced recipe with all options
event.recipes.voidminers.miner('minecraft:ancient_debris', 'minecraft:the_nether', 4)
    .weight(0.5)
    .count(1)
    .allowHigherTiers(false);
```

