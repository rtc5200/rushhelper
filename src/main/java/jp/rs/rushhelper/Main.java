/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.rs.rushhelper;

import java.util.logging.Logger;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rushhelper.Command.CommandAccepter;
import jp.rs.rushhelper.Config.JsonConfigHandler;
import jp.rs.rushhelper.Listener.ListenerLeader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author MainUser
 */
public class Main extends JavaPlugin {
    private JsonConfigHandler jconfig;
    public final Logger log = this.getLogger();
    private GameHandler gh;

    @Override
    public void onEnable() {
        log.info("Starting to load....");
            log.info("loading config from json file....");
            jconfig = new JsonConfigHandler(this);
            jconfig.load();
            log.info("load completed.");
        log.info("load completed.");
        log.info("preparing...");
            gh = new GameHandler(this);
        log.info("Preparetion completed.");
        log.info("Load completed.");
        log.info("Registeration starting...");
            log.info("registering Listeners...");
                ListenerLeader ll = new ListenerLeader(this);
                ll.register();
            log.info("Listener registeration completed.");
            log.info("registering CommandExecutor...");
                this.getCommand("rh").setExecutor(new CommandAccepter(this));
            log.info("CommandExecutor registeration completed.");
        log.info("registeration completed.");
        log.info(RSTeamAPI.testMessage);
    }
    public JsonConfigHandler getJConfigHandler(){
        return jconfig;
    }
    public GameHandler getGameHandler(){
        return gh;
    }
}
