package jp.rs.rushhelper.Listener;

import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.GameHandler.GameStatus;
import jp.rs.rushhelper.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
    public void oonCraft(PrepareItemCraftEvent e)
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
}
