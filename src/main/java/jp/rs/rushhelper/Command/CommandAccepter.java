package jp.rs.rushhelper.Command;

import jp.rs.rushhelper.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author MainUser
 */
public class CommandAccepter implements CommandExecutor {

    protected final Main plugin;

    public CommandAccepter(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rh")) {
            if (args.length >= 1) {
                RHCommandEXE exe = null;
                if (args[0].equalsIgnoreCase("ac")) {
                    exe = new AutoClassifyCommand(plugin, sender, cmd, args);
                } else if (args[0].equalsIgnoreCase("bu")) {
                    exe = new BreakUpCommand(plugin, sender, cmd, args);
                } else if (args[0].equalsIgnoreCase("tp")) {
                    exe = new TeleportCommand(plugin, sender, cmd, args);
                } else if(args[0].equalsIgnoreCase("start")){
                    exe = new StartCommand(plugin, sender, cmd, args);
                } else if(args[0].equalsIgnoreCase("join")){
                    exe = new JoinCommand(plugin, sender, cmd, args);
                } else if(args[0].equalsIgnoreCase("test"))
                {
                    exe = new TestCommand(plugin,sender,cmd,args);
                }
                if (exe != null) {
                    exe.Execute();
                    return true;
                }
                sender.sendMessage("コマンドが存在しません。");
                return true;
            }
            sender.sendMessage("引数が足りません。");
            return true;
        }
        return false;
    }

}
