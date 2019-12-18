package studios.hawkeyegame.hackweekbot.core.plugin;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

public class PluginManager {

    private ConcurrentMap<String, IPlugin> plugins;

    public PluginManager() {
        plugins = Maps.newConcurrentMap();
    }

    public IPlugin getPlugin(String name) {
        for (String s : plugins.keySet()) {
            if (name.equalsIgnoreCase(s)) {
                return plugins.get(s);
            }
        }

        return null;
    }

    public void registerPlugin(IPlugin plugin) {
        plugins.put(plugin.getName(), plugin);
        //HGSBCore.getLogger().log(plugin, LogType.INFO, "has been successfully registered...");
        enablePlugin(plugin);
    }

    public boolean isRegistered(String label) {
        return plugins.keySet().contains(label);
    }

    public Collection<IPlugin> getPlugins() {
        return plugins.values();
    }

    public String getPluginName(IPlugin plugin) {
        return plugin.getName();
    }

    public void enablePlugin(IPlugin plugin) {
        plugin.onEnable();
        //HGSBCore.getLogger().log(plugin, LogType.INFO, "has been successfully enabled...");
    }

    public void disablePlugin(IPlugin plugin) {
        plugin.onDisable();
    }

    public boolean isEnabled(String plugin) {
        return isEnabled(getPlugin(plugin));
    }

    public boolean isEnabled(IPlugin plugin) {
        return plugin.isEnabled();
    }
}
