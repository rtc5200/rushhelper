package jp.rs.rushhelper.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam.RSTeamColor;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.Utils.Messanger.PopType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class AutoClassifier {

    private final SbManager smr;
    private final ArrayList<Player> players = new ArrayList<>();
    public boolean success;

    public AutoClassifier() {
        smr = RSTeamAPI.getInstance().getSbManager();
        success = false;
    }

    public void process() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                players.add(p);
            }
        }
        if (!players.isEmpty()) {
            Collections.shuffle(players, new Random());
            for (int i = 0; i < players.size(); i++) {
                if(i % 2 == 0){
                    smr.getTeam(RSTeamColor.RED).addPlayer(players.get(i));
                    continue;
                }
                smr.getTeam(RSTeamColor.BLUE).addPlayer(players.get(i));
            }
            if(smr.getTeam(RSTeamColor.RED).getNumberOfMembers() > 0)
            {
                success = true;
                Messanger msgr;
                String[] msgs = new String[]{
                    RSTeamColor.RED.ColoredString() + "チーム人数 : " + smr.getTeam(RSTeamColor.RED).getNumberOfMembers(),
                    RSTeamColor.BLUE.ColoredString() + "チーム人数 : " + smr.getTeam(RSTeamColor.BLUE).getNumberOfMembers()
                };
                msgr = new Messanger(PopType.SURROUNDED,msgs);
                msgr.process();
            }
        }
    }

}
