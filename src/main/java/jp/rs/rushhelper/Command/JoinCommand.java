package jp.rs.rushhelper.Command;

import java.util.logging.Level;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam.RSTeamColor;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.GameHandler.GameStatus;
import jp.rs.rushhelper.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class JoinCommand extends RHCommandEXE {
    private final SbManager smr;
    public JoinCommand(Main plugin, CommandSender sender, Command cmd, String[] args) {
        super(plugin, sender, cmd, args);
        smr = RSTeamAPI.getInstance().getSbManager();
    }
    @Override
    protected void ExecuteFromPlayer() {
        Player p = (Player) sender;
        if (p.isOp()) {
            if (args.length < 2) {
                Reject(1);
                return;
            } else if (args.length == 2) {
                Player t = Bukkit.getPlayerExact(args[1]);
                if (t == null) {
                    p.sendMessage("プレイヤー名が不正です。");
                    Report(Level.SEVERE,"プレイヤー名が不正です。");
                    return;
                }
                if (plugin.getGameHandler().getStatus().equals(GameStatus.STARTING)) {
                    if(smr.getTeam(RSTeamColor.RED).getNumberOfMembers() <= 
                            smr.getTeam(RSTeamColor.BLUE).getNumberOfMembers())
                    {
                        smr.getTeam(RSTeamColor.RED).addPlayer(p);
                        return;
                    }
                    smr.getTeam(RSTeamColor.BLUE).addPlayer(p);
                    return;
                }
                p.sendMessage("途中参加可能期限を過ぎています。");
                Report(Level.SEVERE,"途中参加可能期限を過ぎています。");
                return;
            }
            Reject(1);
            return;
        }
        Reject(0);
    }

    @Override
    protected void ExecuteFromBlockCommand() {
        if (args.length < 2) {
            Report(Level.SEVERE,"引数が足りません。");
            return;
        } else if (args.length == 2) {
            Player t = Bukkit.getPlayerExact(args[1]);
            if (t == null) {
                Report(Level.SEVERE,"プレイヤー名が不正です。");
                return;
            }
            if (plugin.getGameHandler().getStatus().equals(GameStatus.STARTING)) {
                if(smr.getTeam(RSTeamColor.RED).getNumberOfMembers() <= 
                            smr.getTeam(RSTeamColor.BLUE).getNumberOfMembers())
                    {
                        smr.getTeam(RSTeamColor.RED).addPlayer(t);
                        return;
                    }
                    smr.getTeam(RSTeamColor.BLUE).addPlayer(t);
                    return;
            }
            Report(Level.SEVERE,"途中参加可能期限を過ぎています。");
            return;
        }
        Reject(1);
    }
}
