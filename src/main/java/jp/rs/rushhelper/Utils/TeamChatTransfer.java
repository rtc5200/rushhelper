package jp.rs.rushhelper.Utils;

import java.util.logging.Logger;
import jp.rs.rsteamapi.scoreboard.RSTeam;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class TeamChatTransfer {
    private final RSTeam team;
    private final String msg;
    private final Logger log;
    public TeamChatTransfer(RSTeam team,Player player,String msg){
        this.team = team;
        this.msg ="[" + team.getTeamColor().ColoredString() +  "]<" + player.getDisplayName() + ">" + msg;
        this.log = Logger.getGlobal();
    }
    public void process(){
        team.getFunction().sendMessage(msg);
        log.info(msg);
    }
    
}
