# Ye Olde Boomstick

A Forge 1.20.1 musket mod focused on readable, semi-arcade battlefield combat.

## Gameplay

- Left click with an armed musket to fire after its ignition cue.
- Hold right click with an empty musket for 15 seconds to reload. Releasing or unequipping it cancels the reload and its sound. Reloading consumes no ammunition.
- A completed reload enters the armed stage and plays an audible confirmation. The musket stays armed until the next left click fires it.
- Musket balls deal 10 damage, travel at seven blocks per tick, and use a controlled three-degree spread cone.
- The bayonet gives the musket 6 total melee damage.
- Custom drifting smoke grows denser with nearby volleys. Five short-lived sparks appear at the muzzle.
- Listeners at least 40 blocks away receive the distant gunshot recording.
- Close gunshots vary by up to 5% in pitch. Distant reports vary by up to 12.5%.

## Recruits compatibility

Recruits crossbowmen automatically gain musket handling when a musket is placed in their inventory. They switch to it for an existing target or strategic-fire position, aim it in the crossbow pose, fire at range, reload without ammunition, bayonet enemies within two blocks while empty, and shoulder the weapon after 2.5 seconds without an action.

Recruits is an optional dependency. Ye Olde Boomstick also runs on its own.

## Development

Use Java 17 and run:

```sh
./gradlew build
```

The built mod is written to `build/libs/`.
