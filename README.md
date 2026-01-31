# World Modifier Mod

A Minecraft Forge mod for 1.20.1 that allows you to customize world generation settings. Control which biomes generate, adjust sea level, and create unique world configurations.

## Features

- **Biome Filtering**: Only allow specific biomes to generate in your world
- **Smart Replacement**: Non-whitelisted biomes are replaced with biomes from your whitelist, maintaining natural-sized regions
- **Custom Sea Level**: Adjust the world's sea level to create flooded or drained worlds
- **Hot Reload**: Configuration changes take effect without restarting the game
- **Mod Support**: Works with modded biomes using their full resource locations

## Requirements

- Minecraft 1.20.1
- Forge 47.2.0 or higher
- Java 17

## Installation

1. Download the mod JAR file
2. Place it in your Minecraft `mods` folder
3. Launch the game with Forge

## Configuration

After first launch, a configuration file will be created at:
```
config/worldmodifier-common.toml
```

### Configuration Options

#### `enabled` (default: `true`)
Master toggle for the mod. Set to `false` to disable all modifications.

#### `whitelistedBiomes` (default: ocean biomes)
List of biomes that are allowed to generate. Use full resource locations.

**Important**: If this list is empty, all biomes are allowed (whitelist is disabled).

When a non-whitelisted biome would generate, it is replaced with a biome from your whitelist. The replacement is consistent based on location, so biome regions maintain natural sizes.

#### `seaLevel` (default: `100`)
Custom sea level for world generation. Default Minecraft sea level is 63.

#### `bedrockLevel` (default: `0`)
Y level where bedrock generates (bottom of the world). Default Minecraft is -64.

## Example Configurations

### Plains-Only World
```toml
[general]
enabled = true
whitelistedBiomes = ["minecraft:plains", "minecraft:river", "minecraft:ocean"]
seaLevel = 63
```

### Forest Survival
```toml
[general]
enabled = true
whitelistedBiomes = [
    "minecraft:forest",
    "minecraft:birch_forest",
    "minecraft:dark_forest",
    "minecraft:flower_forest",
    "minecraft:river",
    "minecraft:ocean"
]
```

### Desert Challenge
```toml
[general]
enabled = true
whitelistedBiomes = [
    "minecraft:desert",
    "minecraft:badlands",
    "minecraft:eroded_badlands",
    "minecraft:river",
    "minecraft:warm_ocean"
]
```

### Winter World
```toml
[general]
enabled = true
whitelistedBiomes = [
    "minecraft:snowy_plains",
    "minecraft:snowy_taiga",
    "minecraft:snowy_slopes",
    "minecraft:frozen_peaks",
    "minecraft:ice_spikes",
    "minecraft:frozen_river",
    "minecraft:frozen_ocean",
    "minecraft:deep_frozen_ocean"
]
```

## Common Vanilla Biomes

Here's a reference list of common biome IDs:

| Category | Biomes |
|----------|--------|
| **Plains/Meadow** | `minecraft:plains`, `minecraft:sunflower_plains`, `minecraft:meadow` |
| **Forest** | `minecraft:forest`, `minecraft:birch_forest`, `minecraft:dark_forest`, `minecraft:flower_forest`, `minecraft:cherry_grove` |
| **Taiga** | `minecraft:taiga`, `minecraft:old_growth_pine_taiga`, `minecraft:old_growth_spruce_taiga` |
| **Jungle** | `minecraft:jungle`, `minecraft:sparse_jungle`, `minecraft:bamboo_jungle` |
| **Desert/Badlands** | `minecraft:desert`, `minecraft:badlands`, `minecraft:eroded_badlands`, `minecraft:wooded_badlands` |
| **Snowy** | `minecraft:snowy_plains`, `minecraft:snowy_taiga`, `minecraft:snowy_slopes`, `minecraft:ice_spikes`, `minecraft:frozen_peaks`, `minecraft:jagged_peaks` |
| **Mountain** | `minecraft:stony_peaks`, `minecraft:grove`, `minecraft:snowy_slopes`, `minecraft:jagged_peaks`, `minecraft:frozen_peaks` |
| **Swamp** | `minecraft:swamp`, `minecraft:mangrove_swamp` |
| **Ocean** | `minecraft:ocean`, `minecraft:deep_ocean`, `minecraft:warm_ocean`, `minecraft:lukewarm_ocean`, `minecraft:cold_ocean`, `minecraft:frozen_ocean` |
| **River/Beach** | `minecraft:river`, `minecraft:frozen_river`, `minecraft:beach`, `minecraft:stony_shore` |
| **Cave** | `minecraft:deep_dark`, `minecraft:dripstone_caves`, `minecraft:lush_caves` |

## Tips

1. **Always include rivers and oceans** in your whitelist if you want water bodies, otherwise all water areas will be replaced with random biomes from your whitelist.

2. **Test on a new world** - the whitelist only affects newly generated chunks.

3. **For modded biomes**, use the format `modid:biome_name` (e.g., `biomesoplenty:redwood_forest`).

4. **Hot reload**: Edit the config file while the game is running, and changes will apply to newly generated chunks.

## Troubleshooting

**Q: My world looks completely flat/uniform**
A: Check that your whitelist contains biomes that actually exist.

**Q: Changes aren't taking effect**
A: Changes only affect newly generated chunks. Travel to unexplored areas to see the new configuration.

**Q: The mod isn't working at all**
A: Make sure `enabled = true` in your config and that your whitelist is not empty.

## Building from Source

```bash
./gradlew build
```

The built JAR will be in `build/libs/`.

## License

This project is open source.
