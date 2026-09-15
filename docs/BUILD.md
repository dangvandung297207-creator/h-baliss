# Building and releasing Herbalist's Craft

## What you need

- Java 21 (the build targets Java 21 and NeoForge 1.21.1)
- Internet access on the first build: Gradle downloads NeoForge, Minecraft and the libraries
- Nothing else — the wrapper is committed, so `./gradlew` is enough

## Build the jar locally

```bash
./gradlew build
```

The mod jar lands in `build/libs/herbalistscraft-1.0.0.jar`. Drop it into a NeoForge
1.21.1 `mods/` folder with [NeoForge 21.1.250](https://neoforged.net/) or newer 21.1.x.

Useful neighbours:

| Command | What it does |
| --- | --- |
| `./gradlew build` | compile, process resources and package the jar |
| `./gradlew runClient` | launch a development client with the mod loaded |
| `./gradlew runServer` | launch a development server |
| `./gradlew runData` | regenerate datapack JSON into `src/generated/resources` from the game's own data generators |
| `./gradlew runGameTestServer` | run the game tests, if any are added |

## Regenerating the content

Every herb, medicine, item, texture, model, recipe, loot table, advancement, sound and the
registration Java is produced from `tools/content/*.json`:

```bash
python3 tools/generate.py           # write into this repository
python3 tools/generate.py --check   # generate into a temp directory and report only
```

It needs pillow (and numpy and soundfile for the procedural sounds):

```bash
python3 -m venv /tmp/venv
/tmp/venv/bin/pip install pillow numpy soundfile
/tmp/venv/bin/python tools/generate.py
```

The generator writes:

- `src/main/java/com/herbalistscraft/registry/{ModHerbs,ModMedicines,ModItems,ModBlocks}.java` — registration
- `src/main/java/com/herbalistscraft/client/GuiLayout.java` — screen geometry, shared with the GUI textures
- `src/main/resources/assets/herbalistscraft/**` — textures, models, blockstates, particles, lang, sounds
- `src/main/resources/data/herbalistscraft/{herb,medicine}/**` — the synced datapack registries and recipes
- `src/generated/resources/data/**` — tags, worldgen, loot tables, advancements

Never edit the generated Java by hand: change the content JSON and re-run the generator.

## Releases

`.github/workflows/build.yml` builds the mod on every push to `main` or an `arena/**` branch and
uploads the jar as a workflow artifact. Push a version tag and the same workflow publishes a
GitHub release with the jar attached:

```bash
git tag -a v1.0.0 -m "Herbalist's Craft 1.0.0"
git push origin v1.0.0
```

The release is created from the tag, so the jar always matches the tagged source. While the build
is failing, the workflow commits the compiler output to `ci/last-build.log` on the branch, which
makes build failures readable without downloading logs.

## Version matrix

| Component | Version |
| --- | --- |
| Mod | Herbalist's Craft 1.0.0 |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.250 (`[21.1,)`) |
| Java | 21 |
| Optional | Tough As Nails (temperature, hydration, seasons), detected at runtime |
