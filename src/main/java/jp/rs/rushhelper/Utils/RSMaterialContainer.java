package jp.rs.rushhelper.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author MainUser
 */
public class RSMaterialContainer {
    public final Material material;
    private final byte meta;
    
    public RSMaterialContainer(Material m,byte meta)
    {
        this.material = m;
        this.meta = meta;
    }
    public boolean hasMetadata()
    {
        return (meta != (byte)0);
    }
    public byte getMeta()
    {
        return meta;
    }
    public boolean EqualsTo(Block b)
    {
        if(material == b.getType())
        {
            if(meta == 0)
            {
                return true;
            }
            return meta == b.getData();
        }
        return false;
    }
    
}
