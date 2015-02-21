package jp.rs.rushhelper;

import java.util.HashMap;
import org.bukkit.entity.Player;

/**
 *
 * @author MainUser
 */
public class PlayerTimeContainer {
    HashMap<Player,Long> list;
    public PlayerTimeContainer(){
        list = new HashMap();
    }
    public void record(Player p){
        list.put(p, System.currentTimeMillis());
    }
    public boolean isPassed(Player p,Long limit){
        Long last = (list.containsKey(p))?list.get(p) : 0l;
        return System.currentTimeMillis() - last > limit;
    }
    public void clear(){
        list.clear();
    }
}
