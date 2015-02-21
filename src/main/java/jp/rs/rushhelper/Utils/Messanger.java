package jp.rs.rushhelper.Utils;

import java.util.Arrays;
import jp.rs.rsteamapi.scoreboard.RSTeam.RSTeamColor;
import static jp.rs.rushhelper.Utils.Messanger.PopType.SURROUNDED;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class Messanger {
    private final PopType type;
    private final String[] msgs;

    public Messanger(PopType type, String[] msgs) {
        this.type = type;
        this.msgs = Arrays.copyOf(msgs, msgs.length);
    }
    public Messanger(PopType type,String msgs){
        this.type = type;
        this.msgs = msgs.split("\n");
    }
    public void process(int first_index, int last_index) {
        String fence = ChatColor.GREEN.toString();
        if (type.equals(SURROUNDED)) {
            for (int c = 0; c < 80; c++) {
                fence += "-";
            }
            Bukkit.broadcastMessage(fence);
        }
        for (int i = first_index; i <= last_index; i++) {
            Bukkit.broadcastMessage(msgs[i]);
        }
        /*if(type.equals(SURROUNDED)){
            Bukkit.broadcastMessage(fence);
        }*/
    }
    public void process(){
        process(0,msgs.length - 1);
    }
    public void replaceformatting(RSTeamColor color,Player p){
        replaceformatting(p);
        replaceformatting(color);
    }
    public void replaceformatting(Player p){
        for(int i = 0;i < msgs.length;i++){
            msgs[i] = msgs[i].replaceAll("%p",p.getDisplayName());
        }
    }
    public void replaceformatting(RSTeamColor color){
        for(int i = 0;i < msgs.length;i++){
            msgs[i] = msgs[i].replaceAll("%t",color.ColoredString());
        }
    }
        

    public enum PopType {
        NORMAL, SURROUNDED
    }
}
