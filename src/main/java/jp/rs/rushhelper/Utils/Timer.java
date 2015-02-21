package jp.rs.rushhelper.Utils;

import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rushhelper.GameHandler;
import jp.rs.rushhelper.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class Timer {
    private final Main plugin;
    private final Long time;
    private Long time_remain;
    private boolean cancelled = false;
    
    public Timer(Main plugin,Long time){
        this.plugin = plugin;
        this.time = time;
        time_remain = time;
    }
    public void Start(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,new Count(),20,20);
    }
    public void setCancelled(boolean cancel){
        this.cancelled = cancel;
    }
    private class Count implements Runnable{

        @Override
        public void run() {
            if(!cancelled && time_remain <  0){
                cancelled = true;
                plugin.getGameHandler().setStatus(GameHandler.GameStatus.PROGRESS);
                Bukkit.broadcastMessage("ゲーム開始から3分経過したため、途中参加は無効になります。");
            }
            if(!cancelled){
                time_remain--;
                setExpOfNoTeamPlayer(time_remain);
            } 
        }
        
        private void setExpOfNoTeamPlayer(Long l){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(RSTeamAPI.getInstance().getSbManager().getTeam(p) == null){ //!plugin.getSbManager().isTeamMember(p)
                    p.setLevel(Integer.parseInt(l.toString()));
                }
            }
        }
        
    }
        
    
    
}
