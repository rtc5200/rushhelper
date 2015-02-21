package jp.rs.rushhelper.Command;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.rs.rushhelper.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public abstract class RHCommandEXE {
    protected final Main plugin;
    protected CommandSender sender;
    private final Command cmd;
    protected final String[] args;
    private final String cmd_line;
    public RHCommandEXE(Main plugin,CommandSender sender,Command cmd,String[] args)
    {
        this.plugin = plugin;
        this.sender = sender;
        this.cmd = cmd;
        this.args = args;
        String args_temp =  Arrays.toString(args);
        args_temp = args_temp.substring(1,args_temp.length() - 1);
        args_temp = args_temp.replaceAll(", ", " ");
        cmd_line = "/" + cmd.getName() + " " + args_temp;
    }
    protected void Execute()
    {
        if (sender instanceof Player) {
            ExecuteFromPlayer();
        } else if (sender instanceof BlockCommandSender) {
            ExecuteFromBlockCommand();
        } else {
            ExecuteFromConsole();
        }
    }
    protected void ExecuteFromPlayer(){};
    protected void ExecuteFromBlockCommand(){};
    protected final void ExecuteFromConsole(){
        sender.sendMessage("RushHelperはコンソールからのコマンドは対応しておりません。");
    };
    protected final void Reject(int id){
        String msg = null;
        switch(id){
            case 0:msg = ChatColor.RED + "このコマンドはOPのみ実行可能です";break;
            case 1:msg = ChatColor.RED + "引数が不正です。";break;
            default:break;
        }
        if(sender instanceof Player){
            sender.sendMessage(msg);
        }
        Report(Level.SEVERE,msg);
    }
    protected final void Report(Level lv,String msg){
        Logger log = plugin.log;
        msg = ChatColor.stripColor(msg);
        msg = "[" + sender.getName() + " =={" + cmd_line + "}]==>" + msg + "}";
        log.log(lv,msg);    
    }
    protected final void Report(Level lv,String msg,String par1){
        Logger log = plugin.log;
        msg = ChatColor.stripColor(msg);
        msg = "[" + sender.getName() + " =={" + cmd_line + "}]==>" + msg + "}";
        log.log(lv,msg,par1);
    }
    protected final void Report(Level lv,String msg,Object[] pars){
        Logger log = plugin.log;
        msg = ChatColor.stripColor(msg);
        msg = "[" + sender.getName() + " =={" + cmd_line + "}]==>" + msg + "}";
        log.log(lv,msg,pars);
    }
}
