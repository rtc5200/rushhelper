package jp.rs.rushhelper.Command;

import java.util.logging.Level;
import jp.rs.rushhelper.Main;
import jp.rs.rushhelper.Utils.AutoClassifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class AutoClassifyCommand extends RHCommandEXE {

    public AutoClassifyCommand(Main plugin, CommandSender sender, Command cmd, String[] args) {
        super(plugin, sender, cmd, args);
    }

    @Override
    protected void ExecuteFromPlayer()
    {
        Player p = (Player)sender;
        if(p.isOp())
        {
            AutoClassifier ac = new AutoClassifier();
            ac.process();
            if (ac.success) {
                p.sendMessage("チーム分けを行いました。");
                this.Report(Level.INFO, "チーム分けに成功しました。");
                return;
            }
            p.sendMessage("チーム分けに失敗しました。");
            this.Report(Level.SEVERE, "チーム分けに失敗しました。");
            return;
        }
        Reject(0);
    }
    @Override
    protected void ExecuteFromBlockCommand()
    {
        AutoClassifier ac = new AutoClassifier();
        ac.process();
        if(ac.success){
            this.Report(Level.INFO, "チーム分けに成功しました。");return;
        }
        this.Report(Level.SEVERE, "チーム分けに失敗しました。");
    }
    
}
