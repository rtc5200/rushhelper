package jp.rs.rushhelper.Command;

import jp.rs.rushhelper.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class TestCommand extends RHCommandEXE{

    public TestCommand(Main plugin, CommandSender sender, Command cmd, String[] args) {
        super(plugin, sender, cmd, args);
    }
    @Override
    public void ExecuteFromPlayer()
    {
        Player p = (Player)sender;
        Block base = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
        BlockState head = base.getRelative(BlockFace.DOWN).getState();
        BlockState head_d = head.getBlock().getRelative(BlockFace.DOWN).getState();
        BlockState foot = head.getBlock().getRelative(BlockFace.SOUTH).getState();
        BlockState foot_d = foot.getBlock().getRelative(BlockFace.DOWN).getState();
        head_d.setType(Material.DIRT);
        foot_d.setType(Material.DIRT);
        head_d.update();
        foot_d.update();
        
        head.setType(Material.BED_BLOCK);
        foot.setType(Material.BED_BLOCK);
        head.setRawData((byte) ((byte) 0x2 | (byte) 0x8));
        foot.setRawData((byte) 0x2);
        head.update(true,true);
        foot.update(true,false);
    }
    
}
