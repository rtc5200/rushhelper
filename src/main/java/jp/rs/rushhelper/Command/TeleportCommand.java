package jp.rs.rushhelper.Command;

import java.util.logging.Level;
import java.util.logging.Logger;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam.RSTeamColor;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.Main;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class TeleportCommand extends RHCommandEXE {
    SbManager smr;
    public TeleportCommand(Main plugin, CommandSender sender, Command cmd, String[] args) {
        super(plugin, sender, cmd, args);
        smr = RSTeamAPI.getInstance().getSbManager();
    }

    

    @Override
    protected void ExecuteFromPlayer() {
        Player p = (Player) sender;
        if (p.isOp()) {
            if (args.length < 4) {
                p.sendMessage("引数が不足しています。");
                return;
            } else if (args.length == 4) {
                Location loc = null;
                try {
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);
                    loc = new Location(p.getWorld(), x, y, z);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Reject(1);
                    return;
                }
                smr.getTeam(RSTeamColor.RED).getFunction().teleport(loc);
                smr.getTeam(RSTeamColor.BLUE).getFunction().teleport(loc);
                p.sendMessage("(" + loc.getX() + "," + loc.getY() + ","
                        + loc.getZ() + ")に両チームテレポートしました。");
                Report(Level.INFO,"({0},{1},{2})に両チームテレポートしました。",
                        new Double[]{loc.getX(),loc.getY(),loc.getZ()});
                return;
            } else if (args.length == 5) {
                RSTeamColor color = null;
                Location loc = null;
                try {
                    color = RSTeamColor.valueOf(args[1].toUpperCase());
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    loc = new Location(p.getWorld(), x, y, z);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Reject(1);
                    return;
                }
                smr.getTeam(color).getFunction().teleport(loc);
                p.sendMessage(color.Localize() + "チームを" + "(" + loc.getX() + "," + loc.getY() + ","
                        + loc.getZ() + ")にテレポートしました。");
                Report(Level.INFO,"{0}チームを({1},{2},{3})にテレポートしました。",
                        new Object[]{color.Localize(),loc.getX(),loc.getY(),loc.getZ()});
                return;
            }
            Reject(1);
            return;
        }
        Reject(0);
    }

    @Override
    protected void ExecuteFromBlockCommand() {
        Logger log = plugin.log;
        BlockCommandSender b = (BlockCommandSender) sender;
        if (args.length < 4) {
            Reject(1);
            return;
        } else if (args.length == 4) {
            Location loc = null;
            try {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                loc = new Location(b.getBlock().getWorld(), x, y, z);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Reject(1);
                return;
            }
             smr.getTeam(RSTeamColor.RED).getFunction().teleport(loc);
             smr.getTeam(RSTeamColor.BLUE).getFunction().teleport(loc);
            Report(Level.INFO, "({0},{1},{2})に両チームテレポートしました。", new Double[]{loc.getX(), loc.getY(), loc.getZ()});
            return;
        } else if (args.length == 5) {
            RSTeamColor color = null;
            Location loc = null;
            try {
                color = RSTeamColor.valueOf(args[1].toUpperCase());
                double x = Double.parseDouble(args[2]);
                double y = Double.parseDouble(args[3]);
                double z = Double.parseDouble(args[4]);
                loc = new Location(b.getBlock().getWorld(), x, y, z);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Reject(1);
                return;
            }
            smr.getTeam(color).getFunction().teleport(loc);
            Report(Level.INFO, "{0}チームを" + "({1},{2},{3}))にテレポートしました。", new Object[]{color.Localize(), loc.getX(), loc.getY(), loc.getZ()});
            return;
        }
        Reject(1);
    }
}
