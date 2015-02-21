package jp.rs.rushhelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jp.rs.rsteamapi.RSTeamAPI;
import jp.rs.rsteamapi.scoreboard.RSTeam;
import jp.rs.rsteamapi.scoreboard.RSTeam.RSTeamColor;
import jp.rs.rsteamapi.scoreboard.SbManager;
import jp.rs.rushhelper.Config.JsonConfigHandler.ConfigLocationType;
import jp.rs.rushhelper.Config.JsonConfigHandler.ConfigMsgType;
import jp.rs.rushhelper.GameHandler.GameStatus;
import jp.rs.rushhelper.Utils.Messanger;
import jp.rs.rushhelper.Utils.Messanger.PopType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 *
 * @author MainUser
 */
public class BedManager implements Listener {

    private final Main plugin;
    private final GameHandler handler;
    private final Location rbl;
    private final Location bbl;
    private final SbManager smr;

    public BedManager(Main plugin, GameHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
        smr = RSTeamAPI.getInstance().getSbManager();
        rbl = plugin.getJConfigHandler().getLocation(RSTeamColor.RED, ConfigLocationType.BED);
        bbl = plugin.getJConfigHandler().getLocation(RSTeamColor.BLUE, ConfigLocationType.BED);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected void GenerateBoth() {
        Generate(RSTeamColor.RED);
        Generate(RSTeamColor.BLUE);
    }

    protected void Generate(RSTeamColor color) {
        Block head = (color == RSTeamColor.RED) ? rbl.getBlock() : bbl.getBlock();
        Block foot = head.getRelative(BlockFace.WEST);
        head.setType(Material.BED_BLOCK);
        head.setData((byte) ((byte) 0x3 | (byte) 8));
        foot.setType(Material.BED_BLOCK);
        foot.setData((byte) 0x3);
    }

    protected void DestroyBoth() {
        Destroy(RSTeamColor.RED);
        Destroy(RSTeamColor.BLUE);
    }

    protected void Destroy(RSTeamColor color) {
        Block head = (color == RSTeamColor.RED) ? rbl.getBlock() : bbl.getBlock();
        Block foot = head.getRelative(BlockFace.WEST);
        head.setType(Material.AIR);
        foot.getRelative(BlockFace.WEST).setType(Material.AIR);
    }

    protected boolean isDestroyed(RSTeamColor color) {
        if (color.equals(RSTeamColor.RED)) {
            return !rbl.getBlock().getState().getType().equals(Material.BED_BLOCK);
        }
        return !bbl.getBlock().getState().getType().equals(Material.BED_BLOCK);
    }

    private boolean isSpawnBed(RSTeamColor color, Block b) {
        boolean result = false;
        if (color.equals(RSTeamColor.RED)) {
            if(rbl.getBlock().equals(b) && b.getType().equals(Material.BED_BLOCK)){
                result = true;
            }else if(rbl.getBlock().getRelative(BlockFace.WEST).equals(b) &&
                    b.getType().equals(Material.BED_BLOCK)){
                result = true;   
            }
        }else{
            if(bbl.getBlock().equals(b) && b.getType().equals(Material.BED_BLOCK)){
                result = true;
            }else if(bbl.getBlock().getRelative(BlockFace.WEST).equals(b) &&
                    b.getType().equals(Material.BED_BLOCK)){
                result = true;   
            }
        }
        return result;
    }
    private boolean isNextToSpawnBed(RSTeamColor color,Block b){
        return isSpawnBed(color,b.getRelative(BlockFace.EAST,1));
    }

    private ItemStack getTeamBedItem(final RSTeamColor color) {
        ItemStack result = new ItemStack(Material.BED, 1);
        ItemMeta im = result.getItemMeta();
        im.setDisplayName(color.Localize() + "チームベッド");
        im.setLore(new ArrayList<String>() {
            {
                add(color.Localize() + "チームのベッドから半径3マス内で");
                add("右クリックするとベッド設置");
            }
        });
        result.setItemMeta(im);
        return result;
    }
    @EventHandler
    public void onBedBrokenByPlayer(final BlockBreakEvent e) {
        Block b = e.getBlock();
        Player p = e.getPlayer();
        if (!plugin.getGameHandler().getStatus().equals(GameStatus.AWAIT)) {
            RSTeam PTeam = smr.getTeam(p);
            if (PTeam != null) {
                if (isSpawnBed(RSTeamColor.RED, b)) {
                    if(PTeam.getTeamColor() == RSTeamColor.RED){
                        p.sendMessage("自分のチームのベッドは壊せません。");
                        e.setCancelled(true);
                        return;
                    }
                    Messanger msgr = new Messanger(PopType.SURROUNDED, "赤チームのベッドが%pによって破壊されました。");
                    msgr.replaceformatting(p);
                    msgr.process();
                    e.setCancelled(true);
                    EraseBedDrops(b);
                    b.setType(Material.AIR);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                Item item = e.getBlock().getLocation().getWorld().dropItem(e.getBlock().getLocation(), 
                                        getTeamBedItem(RSTeamColor.RED));
                                item.setVelocity(new Vector(0,0,0));
                            }
                        });
                    return;
                }
                if (isSpawnBed(RSTeamColor.BLUE, b)) {
                    if(smr.getTeam(p).getTeamColor() == RSTeamColor.BLUE){
                        p.sendMessage("自分のチームのベッドは壊せません。");
                        e.setCancelled(true);
                        return;
                    }
                    Messanger msgr = new Messanger(PopType.SURROUNDED, "青チームのベッドが%pによって破壊されました。");
                    msgr.replaceformatting(p);
                    msgr.process();
                    e.setCancelled(true);
                    EraseBedDrops(b);
                    b.setType(Material.AIR);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                Item item = e.getBlock().getLocation().getWorld().dropItem(e.getBlock().getLocation(), 
                                        getTeamBedItem(RSTeamColor.BLUE));
                                item.setVelocity(new Vector(0,0,0));
                            }
                        });
                }
            }
        }
    }
    @EventHandler
    public void onTNTBreak(final EntityExplodeEvent e) {
        List<Block> bl = e.blockList();
        if (!plugin.getGameHandler().getStatus().equals(GameStatus.AWAIT)) {
            if (e.getEntity().getType().equals(EntityType.PRIMED_TNT)) {
                for (Iterator<Block> it = bl.iterator(); it.hasNext();) {
                    Block b = it.next();
                    RSTeamColor destructed = RSTeamColor.RED;
                    Messanger msgr = null;
                    if (isSpawnBed(RSTeamColor.RED, b) || isNextToSpawnBed(RSTeamColor.RED, b)) {
                        EraseBedDrops(b);
                        msgr = new Messanger(PopType.SURROUNDED, 
                                "%tチームのベッドがTNTによって破壊されました。");
                        it.remove();
                        b.setType(Material.AIR);
                        destructed = RSTeamColor.RED;
                    } else if (isSpawnBed(RSTeamColor.BLUE, b) || isNextToSpawnBed(RSTeamColor.BLUE, b)) {
                        EraseBedDrops(b);
                        msgr = new Messanger(PopType.SURROUNDED, "%tチームのベッドがTNTによって破壊されました。");
                        it.remove();
                        b.setType(Material.AIR);
                        destructed = RSTeamColor.BLUE;
                    }
                    if (msgr != null) {
                        msgr.replaceformatting(destructed);
                        msgr.process();
                        final RSTeamColor dest_c = destructed;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                Item item = e.getLocation().getWorld().dropItem(e.getLocation(), getTeamBedItem(dest_c));
                                item.setVelocity(new Vector(0,0,0));
                            }
                        });
                    }
                }
            }
        }
    }
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        ItemStack item = e.getEntity().getItemStack();
        if (plugin.getGameHandler().getStatus() != GameStatus.AWAIT) {
            if (item.getType() == Material.BED && item.getItemMeta().getDisplayName() == null) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onBedItemRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)
                || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (plugin.getGameHandler().getStatus() != GameStatus.AWAIT) {
                RSTeam PTeam = smr.getTeam(p);
                if (PTeam != null) {
                    if (item != null && item.equals(getTeamBedItem(PTeam.getTeamColor()))) {
                        if (isDestroyed(PTeam.getTeamColor())) {
                            if(distance(PTeam.getTeamColor(),p.getLocation()) <= 3d){ 
                                Generate(PTeam.getTeamColor());
                                Messanger msgr = new Messanger(PopType.SURROUNDED, new String[]{
                                    plugin.getJConfigHandler().getMessage(ConfigMsgType.BED_RESTORED)
                                });
                                msgr.replaceformatting(p);
                                msgr.replaceformatting(PTeam.getTeamColor());
                                msgr.process();
                                p.getInventory().removeItem(item);
                                p.updateInventory();
                                return;
                            }
                            p.sendMessage("ベッドが遠すぎます。");
                            return;
                        }
                        p.getInventory().removeItem(item);
                        p.updateInventory();
                    }
                }
            }
        }
    }

    private double distance(RSTeamColor color,Location loc) {
        if(color == RSTeamColor.RED){
            return rbl.distance(loc);
        }
        return bbl.distance(loc);
    }
    private double distance_NextTo(RSTeamColor name,Location loc){
        if(name == RSTeamColor.RED){
            return rbl.getBlock().getRelative(BlockFace.WEST).getLocation().distance(loc);
        }
        return bbl.getBlock().getRelative(BlockFace.WEST).getLocation().distance(loc);
    }

    private void EraseBedDrops(Block b) {
        Block head = (b.getRelative(BlockFace.WEST).getType() == Material.BED_BLOCK)?
                b:b.getRelative(BlockFace.EAST);
        Block foot = (b.getRelative(BlockFace.EAST).getType() == Material.BED_BLOCK)?
                b : b.getRelative(BlockFace.WEST);
        head.getDrops().clear();
        foot.getDrops().clear();
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        ItemStack item = e.getItemInHand();
        Block b = e.getBlockPlaced();
        RSTeam PTeam = smr.getTeam(p);
        if(PTeam != null){
            if(b.getType() == Material.BED_BLOCK){
                if(getTeamBedItem(PTeam.getTeamColor()).equals(item)){
                    p.sendMessage("ベッドを直接設置することはできません。");
                    e.setBuild(false);
                    e.setCancelled(true);
                    p.updateInventory();
                    return;
                }
                e.setBuild(false);
                e.setCancelled(true);
                p.updateInventory();
                return;
            }
            if(distance(RSTeamColor.RED,b.getLocation()) <= 2d
                    || distance_NextTo(RSTeamColor.RED,b.getLocation()) <= 2d){
                p.sendMessage("ベッドの周りにはブロックを置けません。");
                e.setCancelled(true);
                p.updateInventory();
                return;
            }
            if(distance(RSTeamColor.BLUE,b.getLocation()) <= 2d
                    || distance_NextTo(RSTeamColor.BLUE,b.getLocation()) <= 2d){
                p.sendMessage("ベッドの周りにはブロックを置けません。");
                e.setCancelled(true);
                p.updateInventory();
            }  
        }
    }
    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e){
        Player p = e.getPlayer();
        Block bed = e.getBed();
        if(plugin.getGameHandler().getStatus() != GameStatus.AWAIT){
            RSTeam PTeam = smr.getTeam(p);
            if(PTeam != null){
                if(isSpawnBed(RSTeamColor.RED,bed) || isNextToSpawnBed(RSTeamColor.RED,bed)){
                    if(PTeam.getTeamColor() == RSTeamColor.RED){
                        p.sendMessage("自チームのベッドです。防衛してください。");
                        e.setCancelled(true);return;
                    }
                    p.sendMessage("敵チームのベッドです。破壊してください。");
                    e.setCancelled(true);return;
                }
                if(isSpawnBed(RSTeamColor.BLUE,bed) || isNextToSpawnBed(RSTeamColor.BLUE,bed)){
                    if(PTeam.getTeamColor() == RSTeamColor.BLUE){
                        p.sendMessage("自チームのベッドです。防衛してください。");
                        e.setCancelled(true);return;
                    }
                    p.sendMessage("敵チームのベッドです。破壊してください。");
                    e.setCancelled(true);
                }
            }
        }
    }
}
