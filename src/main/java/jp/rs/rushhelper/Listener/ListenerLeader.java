package jp.rs.rushhelper.Listener;

import jp.rs.rushhelper.Main;
import org.bukkit.Bukkit;

/**
 *
 * @author MainUser
 */
public class ListenerLeader {

    Main plugin;

    public ListenerLeader(Main plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Bukkit.getServer().getPluginManager().registerEvents(
                new TeamEventsListener(plugin),plugin);
        Bukkit.getServer().getPluginManager().registerEvents(
                new InGameListener(plugin),plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemBlockListener(plugin), plugin);
    }
    
}
