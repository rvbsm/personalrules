# Personal rules

Gamerules, but personal! No more server-wide `keepInventory`!

## Usage

The mod is server-side, client installation is __optional__ (unless you want to use it in a singleplayer).

Personal rules can be accessed or modified in the same way as gamerules,
with a little difference of using `/personalrule <rule> [value]`.

By default, all personal rules are disabled and all players are using world gamerules.

To disable a personal rule, use `/personalrule <rule> reset`.

## Configuration (`<world>/personalrules.conf`)

A Carpet-like configuration file with required [permission levels][minecraft-wiki-permission-level] per gamerule.
Allowed values are `ops`, `true` (everyone), `false` (disabled),
and permission level in range `0`-`5` (where `0` = everyone, `5` = disabled).

Permission levels can be queried or modified with `/personalrules permission <rule> [permission_level]`.
Reload from the configuration file with `/personalrule permission reload`.

The permission level of 2 is required to be able to access `/personalrules permission` command.

### Default configuration

```
keepInventory                     ops
doMobLoot                         ops
projectilesCanBreakBlocks         ops
doTileDrops                       ops
doEntityDrops                     ops
naturalRegeneration               ops
reducedDebugInfo                  true
doLimitedCrafting                 ops
disableRaids                      ops
doInsomnia                        true
doImmediateRespawn                true
playersNetherPortalDefaultDelay   true
playersNetherPortalCreativeDelay  true
drowningDamage                    ops
fallDamage                        ops
fireDamage                        ops
freezeDamage                      ops
doPatrolSpawning                  ops
doTraderSpawning                  ops
doWardenSpawning                  ops
forgiveDeadPlayers                true
enderPearlsVanishOnDeath          true
tntExplodes                       true
```

[minecraft-wiki-permission-level]: https://minecraft.wiki/w/Permission_level "Permission level – Minecraft Wiki"
