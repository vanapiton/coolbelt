package fi._1up.coolbelt.config;

import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class CoolbeltConfig {
    @ConfigRoot(value = "config", visibleName = "Coolbelt Config", index = 1)
    public final static CoolbeltConfigFields config = new CoolbeltConfigFields();

    // TODO: Make this update properly
    // Shortcut to Accessory API's config
    //@ConfigRoot(value = "accessoryapi", visibleName = "Accessory API Config", index = 2)
    //public static AccessoryAPIConfigFields accessoryConfig = AccessoryAPI.config;
}
