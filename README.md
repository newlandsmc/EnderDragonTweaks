# EnderDragonTweaks

___
Plugin designed for [SemiVanilla-MC](https://github.com/SemiVanilla-MC/SemiVanilla-MC) with the goal to add extra features and tweaks to the ender dragon boss battle.
  * Optional itemdrops that scatter on the island between the endspikefeature and the end portal.
  * Configurable experience per player

## **Downloads**
Downloads can be obtained on the [github actions page.](https://github.com/SemiVanilla-MC/EnderDragonTweaks/actions)

## **Building**

#### Initial setup
Clone the repo using `git clone https://github.com/SemiVanilla-MC/EnderDragonTweaks.git`.

#### Compiling
se the command `./gradlew build --stacktrace` in the project root directory.
The compiled jar will be placed in directory `/build/libs/`.

## **Commands**

| Command             | Description                 | Permission                  |
|---------------------|-----------------------------|-----------------------------|
| /enderdragontweaks  | Reload plugin configuration | `enderdragontweaks.command` |

## **Permissions**

| Permission                  | Description                                    |
|-----------------------------|------------------------------------------------|
| `enderdragontweaks.command` | Required permission to use the reload command. |


## **Configuration**

```yaml
# Magic value used to determine auto configuration updates, do not change this value
config-version: 1

# Optional worldname to be used when there's more than 1 end world.
worldname: ''

# Settings related to the ender dragon
enderdragon:
  # Remove drops added by plugins and or datapacks?
  remove-drops: true
  # Total amount of exp to be distributed among the server
  exp-to-drop: 0
  # Broadcast message when the dragon is killed
  killed: <red>The ender dragon was killed by <players>.
  # Broadcast message when the dragon respawn sequence has started
  spawned: <yellow>The ender dragon has been respawned.
  # Time in ticks between new respawn attempts
  respawn-delay: 600

# Settings related to loot drops and where they are spawned in the world
# The endspike feature is 42 blocks away from the center of the end portal
lootdrops:
  # Minimum x coordinate
  min-x: -39
  # Maximum x coordinate
  max-x: 39
  # Minimum z coordinate
  min-z: -39
  # Maximum z coordinate
  max-z: 39
  # Minimum y coordinate
  min-y: 75
  # Maximum y coordinate
  max-y: 75
  # Minimum amount of drops to distribute in the end island
  min-player-count: 1
  # Upper limit to the amount of drops to distribute in the end island
  # if the total amount of drops is lower, this setting is overriden by that value
  max-player-count: 5

# Bukkit Serialized itemdrops
drops:
  '0':
    ==: org.bukkit.inventory.ItemStack
    v: 2865
    type: NETHERITE_SWORD
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      display-name: '{"color":"gold","text":"Destro''s sword"}'
      enchants:
        KNOCKBACK: 2
        LOOT_BONUS_MOBS: 3
        MENDING: 1
        DURABILITY: 3
  '5':
    ==: org.bukkit.inventory.ItemStack
    v: 2865
    type: WHITE_BANNER
    meta:
      ==: ItemMeta
      meta-type: BANNER
      display-name: '{"color":"gold","translate":"block.minecraft.ominous_banner"}'
      ItemFlags:
        - HIDE_POTION_EFFECTS
      patterns:
        - ==: Pattern
          color: CYAN
          pattern: mr
        - ==: Pattern
          color: LIGHT_GRAY
          pattern: bs
        - ==: Pattern
          color: GRAY
          pattern: cs
        - ==: Pattern
          color: LIGHT_GRAY
          pattern: bo
        - ==: Pattern
          color: BLACK
          pattern: ms
        - ==: Pattern
          color: LIGHT_GRAY
          pattern: hh
        - ==: Pattern
          color: LIGHT_GRAY
          pattern: mc
        - ==: Pattern
          color: BLACK
          pattern: bo
  '6':
    ==: org.bukkit.inventory.ItemStack
    v: 2865
    type: CROSSBOW
    meta:
      ==: ItemMeta
      meta-type: CROSSBOW
      Damage: 461
      charged: false
  '1':
    ==: org.bukkit.inventory.ItemStack
    v: 2865
    type: NETHERITE_SWORD
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      enchants:
        KNOCKBACK: 2
        LOOT_BONUS_MOBS: 3
        MENDING: 1
        DURABILITY: 3
  '2':
    ==: org.bukkit.inventory.ItemStack
    v: 2865
    type: DRAGON_EGG
  '3':
    ==: org.bukkit.inventory.ItemStack
    v: 2865
    type: ELYTRA
  '4':
    ==: org.bukkit.inventory.ItemStack
    v: 2865
    type: ELYTRA
    meta:
      ==: ItemMeta
      meta-type: UNSPECIFIC
      enchants:
        MENDING: 1
        DURABILITY: 3
```

## **Support**

## **License**
[LICENSE](LICENSE)
