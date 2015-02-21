package jp.rs.rushhelper;

import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam.RSTeamColor;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.Config.JsonConfigHandler;
import jp.rs.rushhelper.Config.JsonConfigHandler.ConfigLocationType;
import jp.rs.rushhelper.Config.JsonConfigHandler.ConfigMsgType;
import jp.rs.rushhelper.Utils.Messanger;
import jp.rs.rushhelper.Utils.Messanger.PopType;
import jp.rs.rushhelper.Utils.Timer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author MainUser
 */
public class GameHandler {
    private final Main plugin;
    private final JsonConfigHandler jconfig;
    private GameStatus status;
    private final BedManager bm;
    private Timer timer;
    public PlayerTimeContainer ptc;
    public SbManager smr;
    public GameHandler(Main plugin){
        this.plugin = plugin;
        this.jconfig = plugin.getJConfigHandler();
        bm = new BedManager(plugin,this);
        ptc = new PlayerTimeContainer();
        status = GameStatus.AWAIT;
        smr = RSTeamAPI.getInstance().getSbManager();
    }
    
    public void Start(){
        if(smr.getTeam(RSTeamColor.RED).getNumberOfMembers() == 0){
            Bukkit.broadcastMessage("チーム分けが正しくなされていません。");
            return;
        }
        status = GameStatus.STARTING;
        timer = new Timer(plugin,180L);
        bm.GenerateBoth();
        smr.getTeam(RSTeamColor.RED).getFunction().teleport(jconfig.getLocation(RSTeamColor.RED, ConfigLocationType.START));
        smr.getTeam(RSTeamColor.BLUE).getFunction().teleport(jconfig.getLocation(RSTeamColor.BLUE, ConfigLocationType.START));
        Messanger msgr = new Messanger(Messanger.PopType.SURROUNDED,
        jconfig.getMessage(ConfigMsgType.START));
        msgr.process();
        timer.Start();
        for(Entity e : jconfig.getWorld().getEntitiesByClass(Item.class)){
            ItemStack item = ((Item)e).getItemStack();
            if(item.getType() == Material.BED){
                e.remove();
            }
        }
    }
    public void Judge() {
        if (status != GameStatus.AWAIT) {
            if (smr.getTeam(RSTeamColor.RED).getNumberOfMembers() == 0
                    || smr.getTeam(RSTeamColor.BLUE).getNumberOfMembers() == 0) {
                if (status.equals(GameStatus.PROGRESS) || status.equals(GameStatus.STARTING)) {
                    Stop();
                }
            }
        }

    }
    
    public void Stop(){
        this.status = GameStatus.AWAIT;
        timer.setCancelled(true);
        int rn = smr.getTeam(RSTeamColor.RED).getNumberOfMembers();
        int bn = smr.getTeam(RSTeamColor.BLUE).getNumberOfMembers();
        RSTeamColor win = (rn >= bn)?RSTeamColor.RED : RSTeamColor.BLUE;
        RSTeamColor lose = (win == RSTeamColor.RED)?RSTeamColor.BLUE : RSTeamColor.RED;
        bm.DestroyBoth();
        
        smr.getTeam(win).getFunction().teleport(jconfig.getLocation(win, ConfigLocationType.BED).getWorld().getSpawnLocation());
        smr.getTeam(lose).getFunction().teleport(jconfig.getLocation(lose, ConfigLocationType.BED).getWorld().getSpawnLocation());
        
        smr.getTeam(win).clear();
        smr.getTeam(lose).clear();
        
        ptc.clear();
        Messanger msgr;
        msgr = new Messanger(PopType.SURROUNDED,jconfig.getMessage(ConfigMsgType.ANNI));
        msgr.replaceformatting(lose);
        msgr.process();
        msgr = new Messanger(PopType.SURROUNDED,jconfig.getMessage(ConfigMsgType.WIN));
        msgr.replaceformatting(win);
        msgr.process();
    }
    public boolean BedDestroyed(RSTeamColor color){
        return bm.isDestroyed(color);
    }
    public GameStatus getStatus(){
        return status;
    }
    public void setStatus(GameStatus status){
        this.status = status;
    }
    
    public enum GameStatus{
        AWAIT,STARTING,PROGRESS
    }
}
