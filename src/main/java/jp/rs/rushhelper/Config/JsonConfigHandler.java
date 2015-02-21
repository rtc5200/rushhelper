package jp.rs.rushhelper.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.rs.rsteamapi.scoreboard.RSTeam.RSTeamColor;
import jp.rs.rushhelper.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.material.MaterialData;

/**
 *
 * @author MainUser
 */
public class JsonConfigHandler {

    private final Main plugin;
    private final File file_zahyou;
    private final File file_msgs;
    private final File file_basics;
    private final Gson gson;
    private ConfigValues_zahyou zconf;
    private ConfigValues_msgs mconf;
    private ConfigValues_Basic bconf;
    private final Logger log;

    public JsonConfigHandler(Main main) {
        plugin = main;
        this.file_zahyou = new File(plugin.getDataFolder(), "zahyou.json");
        this.file_msgs = new File(plugin.getDataFolder(),"messages.json");
        this.file_basics = new File(plugin.getDataFolder(),"config.json");
        log = plugin.log;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void load() {
        FileHelper fh = new FileHelper(file_zahyou);
        fh.adjustFile();
        if (fh.created()) {
            ConfigValues_zahyou zconf_temp = new ConfigValues_zahyou();
            writeJson(file_zahyou,zconf_temp);
        }
        fh = new FileHelper(file_msgs);
        fh.adjustFile();
        if(fh.created()){
            ConfigValues_msgs mconf_temp = new ConfigValues_msgs();
            writeJson(file_msgs,mconf_temp);
        }
        fh = new FileHelper(file_basics);
        fh.adjustFile();
        if(fh.created()){
            ConfigValues_Basic bconf_temp = new ConfigValues_Basic(); 
            writeJson(file_basics,bconf_temp);
        }
        zconf = (ConfigValues_zahyou) readJson(file_zahyou,ConfigValues_zahyou.class);
        mconf = (ConfigValues_msgs) readJson(file_msgs,ConfigValues_msgs.class);
        bconf = (ConfigValues_Basic) readJson(file_basics,ConfigValues_Basic.class);
        bconf.ConvertStringToMaterial();
    }

    private void writeJson(File file,Object object) {
        try(JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"))){
            writer.setIndent("   ");
           gson.toJson(object,object.getClass(),writer);
           writer.flush();
           writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @SuppressWarnings("CallToPrintStackTrace")
    private Object readJson(File file,Class<?> cl) {
        Object result = null;
        try(JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file),"UTF-8"))){
            result =  gson.fromJson(reader,cl);
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }
    
    public World getWorld(){
        return zconf.getWorld();
    }
    
    public Location getLocation(RSTeamColor color,ConfigLocationType type){
        return zconf.getLocation(color, type);
    }
    public String getMessage(ConfigMsgType type){
        return mconf.getMsg(type);
    }
    public boolean getNPCProtectionEnable(){
        return bconf.EnableNPCProtection;
    }
    public List<Material> getItemBlockMaterialList(){
        return bconf.getItemBlockMaterials();
    }
    
    
    private class ConfigValues_Basic {
        @SerializedName("NPCの保護を有効化")
        protected boolean EnableNPCProtection = true;
        @SerializedName("右クリックでアイテムを出現させるブロックID")
        protected String ItemBlocks = "41";
        private transient ArrayList<Material> mlist = new ArrayList<>();
        
        public List<Material> getItemBlockMaterials(){
           return Collections.unmodifiableList(mlist);
           
        }
        public void ConvertStringToMaterial(){
            mlist = new ArrayList<>();
            Pattern p = Pattern.compile("([0-9]+)(:([0-9]))?");
            Matcher m;
            for(String s : ItemBlocks.split(",")){
                m = p.matcher(s);
                if(m.matches()){
                    String id = m.group(1);
                    String meta = m.group(3);
                    MaterialData md = new MaterialData(Integer.parseInt(id));
                    if(meta != null){
                        md.setData(Byte.parseByte(meta));
                    }
                    mlist.add(md.getItemType());
                }
            }
        }
        
        
    }
        
    private class ConfigValues_zahyou {
        @SerializedName("ワールド名")
        private String world;
        @SerializedName("赤チームのスタート時のTP座標")
        private Double[] red_start_location;
        @SerializedName("青チームのスタート時のTP座標")
        private Double[] blue_start_location;
        @SerializedName("赤チームのベッドの座標")
        private Double[] red_bed_location;
        @SerializedName("青チームのベッドの座標")
        private Double[] blue_bed_location;
        
        /**
         *@param world world which will be used
         * @param l1 red_start_location
         * @param l2 blue_start_location
         * @param l3 red_bed_location
         * @param l4 blue_bed_location
         */
        public ConfigValues_zahyou(World world, Location l1, Location l2,
                Location l3, Location l4){
            this.world = world.getName();
            this.red_start_location = new Double[]{l1.getX(), l1.getY(), l1.getZ()};
            this.blue_start_location = new Double[]{l2.getX(), l2.getY(), l2.getZ()};
            this.red_bed_location = new Double[]{l3.getX(), l3.getY(), l3.getZ()};
            this.blue_bed_location = new Double[]{l4.getX(), l4.getY(), l4.getZ()};    
        }
        public ConfigValues_zahyou(){
            this.world = "world";
            this.red_start_location = new Double[]{0d,0d,0d};
            this.blue_start_location = new Double[]{0d,0d,0d};
            this.red_bed_location = new Double[]{0d,0d,0d};
            this.blue_bed_location = new Double[]{0d,0d,0d};
        }
        protected World getWorld(){
            return Bukkit.getWorld(world);
        }
        protected Location getLocation(RSTeamColor color,ConfigLocationType type){
            if(color.equals(RSTeamColor.RED)){
                if(type.equals(ConfigLocationType.START)){
                    return DLConvert(red_start_location);
                }
                return DLConvert(red_bed_location);
            }
            if(type.equals(ConfigLocationType.START)){
                return DLConvert(blue_start_location);
            }
            return DLConvert(blue_bed_location);
        }
        protected Double[] getRawData(RSTeamColor color,ConfigLocationType type){
            if(color.equals(RSTeamColor.RED)){
                if(type.equals(ConfigLocationType.START)){
                    return red_start_location;
                }
                return red_bed_location;
            }
            if(type.equals(ConfigLocationType.START)){
                return blue_start_location;
            }
            return blue_bed_location;
        }
        private Location DLConvert(Double[] ds){
            return new Location(getWorld(),ds[0],ds[1],ds[2]);
        }
    }
    private class ConfigValues_msgs{
        @SerializedName("スタート時のメッセージ")
        String msg_on_start;
        @SerializedName("ベッドが再設置された時のメッセージ")
        String msg_on_bed_restored;
        @SerializedName("途中参加時のメッセージ")
        String msg_on_join;
        @SerializedName("全滅した時のメッセージ")
        String msg_on_anni;
        @SerializedName("勝利時メッセージ")
        String msg_on_win;
        /**
         * 
         * @param m1 message on start
         * @param m2 message on bed is restored
         * @param m3 message on join
         * @param m4 message on annihilation
         * @param m5 message on win (after annihilation)
         */
        public ConfigValues_msgs(String m1,String m2,String m3,String m4,String m5){
            this.msg_on_start = m1;
            this.msg_on_bed_restored = m2;
            this.msg_on_join = m3;
            this.msg_on_anni = m4;
            this.msg_on_win = m5;
        }
        public ConfigValues_msgs(){
            this.msg_on_start = ChatColor.AQUA + "ゲームが開始されました!\n健闘を祈ります!";
            this.msg_on_bed_restored = "%tチームのベッドが%pによって再設置されました!";
            this.msg_on_join = ChatColor.BOLD + "%p" +  ChatColor.RESET + "が途中参加しました!";
            this.msg_on_anni = "%tチームが全滅しました。";
            this.msg_on_win  = "%tチームの勝利です。";
        }
        protected String getMsg(ConfigMsgType type){
            switch(type){
                case START:return msg_on_start;
                case BED_RESTORED:return msg_on_bed_restored;
                case JOIN:return msg_on_join;
                case ANNI:return msg_on_anni;
                case WIN:return msg_on_win;
                default:return null;
            }
        }
    }
    public enum ConfigLocationType{
            START,BED
    }
    public enum ConfigMsgType{
        START,BED_RESTORED,JOIN,ANNI,WIN
    }
}
