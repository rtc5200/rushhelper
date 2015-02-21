package jp.rs.rushhelper.Command;

import java.util.logging.Level;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam.RSTeamColor;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.GameHandler;
import jp.rs.rushhelper.GameHandler.GameStatus;
import jp.rs.rushhelper.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class StartCommand extends RHCommandEXE{
    SbManager smr;
    public StartCommand(Main plugin, CommandSender sender, Command cmd, String[] args) {
        super(plugin, sender, cmd, args);
        smr = RSTeamAPI.getInstance().getSbManager();
    }
    
    @Override
    protected void ExecuteFromPlayer(){
        Player p = (Player)sender;
        GameHandler gh = plugin.getGameHandler();
        if(p.isOp()){
            if(gh.getStatus().equals(GameStatus.AWAIT)){
                if(smr.getTeam(RSTeamColor.RED).getNumberOfMembers() == 0){
                    p.sendMessage(RSTeamColor.RED.Localize() + "チームに誰もいません。");
                    Report(Level.SEVERE,"{0}チームに誰もいません。",RSTeamColor.RED.Localize());
                    return;
                }
                plugin.getGameHandler().Start();
                p.sendMessage("ゲームを開始しました。");
                Report(Level.INFO,"ゲームを開始しました。");
                return;
            }
            p.sendMessage("ゲームは進行中です。");
            Report(Level.SEVERE,"ゲームは進行中です。");
            return;
        }
        Reject(0);
    }
    
    @Override
    protected void ExecuteFromBlockCommand(){
        GameHandler gh = plugin.getGameHandler();
        if(gh.getStatus().equals(GameStatus.AWAIT)){
            if(smr.getTeam(RSTeamColor.RED).getNumberOfMembers() == 0){
                    Report(Level.SEVERE,"{0}チームに誰もいません。",RSTeamColor.RED.Localize());
                    return;
                }
                plugin.getGameHandler().Start();
                Report(Level.INFO,"ゲームを開始しました。");
                return;
            }
            Report(Level.SEVERE,"ゲームは進行中です。");
    }
    
}
