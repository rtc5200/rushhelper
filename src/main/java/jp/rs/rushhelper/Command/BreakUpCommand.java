package jp.rs.rushhelper.Command;

import java.util.logging.Level;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam.RSTeamColor;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class BreakUpCommand extends RHCommandEXE {
    private final SbManager smr;

    public BreakUpCommand(Main plugin, CommandSender sender, Command cmd, String[] args) {
        super(plugin, sender, cmd, args);
        smr = RSTeamAPI.getInstance().getSbManager();
    }

   
    @Override
    protected void ExecuteFromPlayer() {
        Player p = (Player) sender;
        if (p.isOp()) {
            if (args.length == 1) {
                smr.getTeam(RSTeamColor.RED).clear();
                smr.getTeam(RSTeamColor.BLUE).clear();
                p.sendMessage("両チーム解散しました。");
                this.Report(Level.INFO, "両チーム解散しました。");
                return;
            } else if (args.length == 2) {
                RSTeamColor color;
                try {
                    color = RSTeamColor.valueOf(args[1]);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    p.sendMessage("チーム名が不正です。");
                    this.Report(Level.SEVERE, "チーム名が不正です。");
                    return;
                }
                smr.getTeam(color).clear();
                p.sendMessage(color.Localize() + "チームを解散しました。");
                this.Report(Level.INFO, "{0}チームを解散しました。",color.Localize());
                return;
            }
            Reject(1);
            return;
        }
        Reject(0);
    }

    @Override
    protected void ExecuteFromBlockCommand() {
        if (args.length == 1) {
            smr.getTeam(RSTeamColor.RED).clear();
            smr.getTeam(RSTeamColor.BLUE).clear();
            this.Report(Level.INFO, "両チーム解散しました。");
            return;
        } else if (args.length == 2) {
            RSTeamColor color;
            try {
                color = RSTeamColor.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                this.Report(Level.SEVERE, "チーム名が不正です。");
                return;
            }
            smr.getTeam(color).clear();
           this.Report(Level.INFO, "{0}チームを解散しました。",color.Localize());
            return;
        }
        Reject(1);
    }

}
