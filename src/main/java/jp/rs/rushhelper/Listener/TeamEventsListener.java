package jp.rs.rushhelper.Listener;

import java.util.logging.Level;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.event.PlayerTeamJoinEvent;
import jp.rs.rsteamapi.event.PlayerTeamQuitEvent;
import jp.rs.rsteamapi.scoreboard.RSTeam;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.Config.JsonConfigHandler.ConfigLocationType;
import jp.rs.rushhelper.Config.JsonConfigHandler.ConfigMsgType;
import jp.rs.rushhelper.GameHandler.GameStatus;
import jp.rs.rushhelper.Main;
import jp.rs.rushhelper.Utils.Messanger;
import jp.rs.rushhelper.Utils.Messanger.PopType;
import jp.rs.rushhelper.Utils.TeamChatTransfer;
import net.minecraft.server.v1_7_R1.EnumClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import net.minecraft.server.v1_7_R1.PacketPlayInClientCommand;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 *
 * @author MainUser
 */
public class TeamEventsListener implements Listener {
    private final Main plugin;
    private final SbManager smr;
    
    public  TeamEventsListener(Main plugin)
    {
        this.plugin = plugin;
        smr = RSTeamAPI.getInstance().getSbManager();
    }
    
    @EventHandler
    public void onTeamJoin(PlayerTeamJoinEvent e)
    {
        Player p = e.getPlayer();
        RSTeam team = e.getTeam();
        ChatColor color = team.getTeamColor().toChatColor();
        changeNameColor(p,color);
        if(plugin.getGameHandler().getStatus() == GameStatus.STARTING){
            Messanger msgr = new Messanger(PopType.SURROUNDED,
            plugin.getJConfigHandler().getMessage(ConfigMsgType.JOIN));
            msgr.replaceformatting(team.getTeamColor(),p);
            msgr.process();
        }
        plugin.log.log(Level.INFO,"{0}???{1}?????????????????????????????????",
                new String[]{p.getDisplayName(),team.getTeamColor().Localize()});
        p.setExp(0f);
        p.setLevel(0);
        p.setHealth(p.getMaxHealth());
    }
    @EventHandler
    public void onTeamLeave(PlayerTeamQuitEvent e)
    {
        Player p = e.getPlayer();
        RSTeam team = e.getTeam();
        changeNameColor(p,ChatColor.RESET);
         Messanger msgr = new Messanger(PopType.SURROUNDED,"%p?????????%t?????????????????????????????????");
         msgr.replaceformatting(team.getTeamColor(),p);
         msgr.process();
         p.getInventory().clear();
         p.setHealth(p.getMaxHealth());
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onLogout(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        RSTeam team = smr.getTeam(p);
        if(team != null){
            team.removePlayer(p);
        }
        changeNameColor(p,ChatColor.RESET);    
    }
    @EventHandler
    public void onLogin(PlayerJoinEvent e)
    {
        changeNameColor(e.getPlayer(),ChatColor.RESET);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(PlayerChatEvent e){
        Player p = e.getPlayer();
        RSTeam team = smr.getTeam(p);
        String msg = e.getMessage();
        if(team != null){
            if(msg.startsWith("!")){
                e.setMessage(msg.substring(1));
                return;
            }
            TeamChatTransfer tct = new TeamChatTransfer(team,p,msg);
            tct.process();
            e.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e){
        final Player p = e.getEntity();
        p.setVelocity(new Vector(0,0,0));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                new Runnable(){
            @Override
            public void run() {
                PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
                ((CraftPlayer)p).getHandle().playerConnection.a(packet);
            } 
        },1L);
        e.setKeepLevel(true);
        e.setDroppedExp(0);
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        RSTeam PTeam = smr.getTeam(p);
        if(plugin.getGameHandler().getStatus().equals(GameStatus.AWAIT))return;
        if(PTeam != null){
            if(plugin.getGameHandler().BedDestroyed(PTeam.getTeamColor())){
                PTeam.removePlayer(p);
                e.setRespawnLocation(p.getWorld().getSpawnLocation());
                plugin.getGameHandler().Judge();
                return;
            } 
            e.setRespawnLocation(plugin.getJConfigHandler().getLocation(
                    PTeam.getTeamColor(),ConfigLocationType.BED));
        }
    }
        
    private void changeNameColor(Player p,ChatColor color)
    {
        String name = color + p.getName() + ChatColor.RESET;
        p.setDisplayName(name);
    }
    
}
