# Ye Olde Boomstick

A Forge 1.20.1 musket mod focused on readable, semi-arcade battlefield combat.

## Gameplay

- Right click with an armed musket to fire after its ignition cue and a subtle recoil shake.
- Hold right click with an empty musket for 15 seconds to reload. The vanilla horse-jump meter replaces the XP bar and changes through five red-to-green stages. Releasing or unequipping pauses at the beginning of the current stage and stops the sound. Reloading consumes no ammunition.
- A completed reload enters the armed stage and plays an audible confirmation. The musket stays armed until the next right click fires it.
- Musket balls deal 24 damage, travel at seven blocks per tick, and use 10 inaccuracy. Recruits below five armor are guaranteed to take lethal damage from a standard full-health hit.
- The bayonet gives the musket 6 total melee damage.
- Custom translucent drifting smoke grows denser with nearby volleys. Five short-lived sparks appear at the muzzle.
- Close and distant reports crossfade over the first 40 blocks. At and beyond that threshold, only the distant report remains.
- Close gunshots vary by up to 12% in pitch. Distant reports vary by up to 12.5%. Volley reports are queued with a short gap so every shot remains audible without exhausting the sound channel pool.

## Recruits compatibility

Players and Recruits crossbowmen hold an armed musket in the true two-handed charged-crossbow pose, using the empty/fired model while aiming. Recruits switch to it for an existing target or strategic-fire position, reload without ammunition, bayonet enemies within two blocks while empty, and shoulder the weapon after 2.5 seconds without an action.

Recruits is an optional dependency. Ye Olde Boomstick also runs on its own.

## Development

Use Java 17 and run:

```sh
./gradlew build
```

The built mod is written to `build/libs/`.
