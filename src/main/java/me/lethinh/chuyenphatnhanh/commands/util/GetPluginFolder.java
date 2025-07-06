package me.lethinh.chuyenphatnhanh.commands.util;

import org.bukkit.plugin.java.JavaPlugin;

public class GetPluginFolder extends JavaPlugin {
    public static String getPluginFolder() {
        return JavaPlugin.getProvidingPlugin(GetPluginFolder.class).getDataFolder().getAbsolutePath();
    }
}
