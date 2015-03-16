package jp.rs.rushhelper.Listener;

import java.util.Iterator;
import java.util.List;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.GameHandler.GameStatus;
import jp.rs.rushhelper.Main;
import jp.rs.rushhelper.Utils.RSMaterialContainer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author MainUser
 */
public class InGameListener implements Listener {

    private final Main plugin;
    private final SbManager smr;

    public InGameListener(Main plugin) {
        smr = RSTeamAPI.getInstance().getSbManager();
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            RSTeam team = smr.getTeam(p);
            if (team == null) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            RSTeam team = smr.getTeam(p);
            if (team == null) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onCraft(PrepareItemCraftEvent e)
    {
        if(e.getViewers().get(0) instanceof Player) 
        {
            Player p = (Player)e.getViewers().get(0);
            if(smr.getTeam(p) != null)
            {
                e.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onNPCDamage(EntityDamageEvent e) {
        if (plugin.getGameHandler().getStatus() != GameStatus.AWAIT) {
            if (plugin.getJConfigHandler().getNPCProtectionEnable()) {
                if (e.getEntity() instanceof NPC) {
                    NPC npc = (NPC) e.getEntity();
                    DamageCause dc = e.getCause();
                    if (dc.equals(DamageCause.ENTITY_ATTACK)
                            || dc.equals(DamageCause.PROJECTILE)
                            || dc.equals(DamageCause.MAGIC)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onTNTExplode(EntityExplodeEvent e)
    {
        List<Block> bl = e.blockList();
         if (!plugin.getGameHandler().getStatus().equals(GameStatus.AWAIT)) {
             if (e.getEntity().getType().equals(EntityType.PRIMED_TNT))
             {
                 for (Iterator<Block> it = bl.iterator(); it.hasNext();) 
                 {
                     Block b = it.next();
                     for(RSMaterialContainer m : plugin.getJConfigHandler().getTNTUnBreakableMaterialList())
                     {
                         if(m.EqualsTo(b))
                         {
                             it.remove();
                         }
                     }
                 }
             }
         }
    }
}
