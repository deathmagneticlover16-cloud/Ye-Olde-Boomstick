# Ye Olde Boomstick

A Forge 1.20.1 musket mod focused on readable, semi-arcade battlefield combat.

## Gameplay

- Hold right click with a loaded musket to fire after its 0.375-second ignition cue.
- Hold right click with an empty musket to reload. Reloads take a random 15–20 seconds and consume no ammunition.
- Musket balls deal 10 damage and have deliberately high spread.
- The bayonet gives the musket 6 total melee damage.
- Smoke density grows with recent shots in the same dimension. Small sparks appear at the muzzle.
- Listeners at least 40 blocks away receive the distant gunshot recording.

## Recruits compatibility

Recruits crossbowmen automatically gain musket handling when a musket is placed in their main hand. They use their existing target, fire at range, reload without ammunition, bayonet enemies within two blocks while empty, and shoulder the weapon after 2.5 seconds without an action.

Recruits is an optional dependency. Ye Olde Boomstick also runs on its own.

## Development

Use Java 17 and run:

```sh
./gradlew build
```

The built mod is written to `build/libs/`.
