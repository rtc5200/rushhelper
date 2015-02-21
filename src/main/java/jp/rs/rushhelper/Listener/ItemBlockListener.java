package jp.rs.rushhelper.Listener;

import java.util.Random;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.GameHandler.GameStatus;
import jp.rs.rushhelper.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author MainUser
 */
public class ItemBlockListener implements Listener{
    private final Main plugin;
    private final SbManager smr;
    
    public ItemBlockListener(Main plugin){
        this.plugin = plugin;
        smr = RSTeamAPI.getInstance().getSbManager();
    }
    @EventHandler
    public void onBlockClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        RSTeam PTeam = smr.getTeam(p);
        Block b = e.getClickedBlock();
        if(PTeam != null){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if(plugin.getJConfigHandler().getItemBlockMaterialList().contains(b.getType())){
                    if(plugin.getGameHandler().ptc.isPassed(p, 5000L)){
                        //p.sendMessage("I'll give you item later");
                        ItemStack item = getGiveItem(); 
                        p.getInventory().addItem(item);
                        p.updateInventory();
                        if(item.getType() != Material.AIR){
                            plugin.getGameHandler().ptc.record(p);
                        } 
                    }
                }
            }
        }
    }
    @EventHandler
    public void ItemBlockBreak(BlockDamageEvent e){
        if(plugin.getGameHandler().getStatus() != GameStatus.AWAIT){
            if(plugin.getJConfigHandler().getItemBlockMaterialList().contains(e.getBlock().getType())){
                e.getPlayer().sendMessage("アイテムブロックは壊せません。");
            e.setCancelled(true);
            }
        }
    }
    private ItemStack getGiveItem(){
        Random r = new Random();
        ItemStack result;
        int v = r.nextInt(10);
        if(v < 6)
        {
            result = new ItemStack(336);
        }else if(v < 9)
        {
            result = new ItemStack(265);
        }else{
            result = new ItemStack(266);
        }
        switch(r.nextInt(10)){
            case 0:result = new ItemStack(336);break;//brick
            case 1:result = new ItemStack(265);break;//iron_ingot
            case 2:result = new ItemStack(266);break;//gold_ingot
            default:result = new ItemStack(Material.AIR);break;
        }  
        return result;
    }
}
