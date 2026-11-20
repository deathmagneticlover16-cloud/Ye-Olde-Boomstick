# Ye Olde Boomstick

A Forge 1.20.1 musket mod focused on readable, semi-arcade battlefield combat.

## Gameplay

- Hold right click with a loaded musket to fire after its 0.375-second ignition cue.
- Hold right click with an empty musket for 15 seconds to reload. Releasing early cancels the reload. Reloading consumes no ammunition.
- A completed reload enters the armed stage and plays an audible confirmation. The musket stays armed until the next right click fires it.
- Musket balls deal 10 damage and use a controlled three-degree spread cone.
- The bayonet gives the musket 6 total melee damage.
- Custom drifting smoke grows denser with nearby volleys. Five short-lived sparks appear at the muzzle.
- Listeners at least 40 blocks away receive the distant gunshot recording.

## Recruits compatibility

Recruits crossbowmen automatically gain musket handling when a musket is placed in their main hand. They use their existing target or strategic-fire position, fire at range, reload without ammunition, bayonet enemies within two blocks while empty, and shoulder the weapon after 2.5 seconds without an action.

Recruits is an optional dependency. Ye Olde Boomstick also runs on its own.

## Development

Use Java 17 and run:

```sh
./gradlew build
```

The built mod is written to `build/libs/`.
