package fi._1up.coolbelt.impl.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

import java.util.Optional;

import static fi._1up.coolbelt.Coolbelt.LOGGER;

public abstract class ModVersionChecker {

    public static boolean isAtLeast(String modId, String version) {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);

        if (container.isPresent()) {
            try {
                Version targetVersion = Version.parse(version);
                return container.get().getMetadata().getVersion().compareTo(targetVersion) >= 0;
            } catch (VersionParsingException err) {
                LOGGER.error("Failed to parse version string: %s", version, err);
                return false;
            }
        }
        return false;
    }
}
