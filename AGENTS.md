# Agent Instructions: Minecraft Forge Modding

## Agent Role
You are an expert Java developer specializing in Minecraft Forge modding.
Your priority is maintaining compatibility with the target Forge version and following clean Registry patterns.

## Tech Stack
- **Language:** Java 17
- **Build System:** Gradle (ForgeGradle)
- **Mapping:** Parchment
- **Target Version:** 1.20.1 (Update this to your version)

## Key Commands
- **Build Mod:** `./gradlew build`
- **Run Client:** `./gradlew runClient`
- **Refresh Dependencies:** `./gradlew --refresh-dependencies`
- **Gen IDE Runs:** `./gradlew genIntellijRuns` (or `genEclipseRuns`)

## Context & Lookup Rules
1. **Source Lookup:** Primary source is `src/main/java`.
2. **Generated/Built Sources:** If a class (like a Forge internal or a multi-part entity) is missing, look in:
    - `build/fg_cache/net/net/minecraftforge/forge/`
3. **Sync Protocol:** Before modifying any file, the agent must perform a read_file operation to ensure the local version matches the agent's internal state. If the file has changed, the agent must incorporate the user's manual changes into the new plan.

## Architecture & Rules
1. **Registry Pattern:** Always use `DeferredRegister` for Blocks, Items, and Entities. Do not use legacy `@ObjectHolder`.
2. **Event Bus:** Distinguish between the **Mod Event Bus** (setup, registry) and the **Forge Event Bus** (gameplay events like `PlayerInteractEvent`).
3. **Sidedness:** Never call Client-only code (like `Minecraft.getInstance()`) in common classes. Use `DistExecutor` or `Proxy` patterns.
4. **Resources:** Always place textures in `src/main/resources/assets/[modid]/textures` and data (loot tables/recipes) in `src/main/resources/data/[modid]`.

## Boundaries
- **Always:** Use `final` for Registry Objects.
- **Ask First:** Before adding a new library dependency to `build.gradle`.
- **Never:** Modify the `gradle-wrapper.properties` or forge-injected core files.