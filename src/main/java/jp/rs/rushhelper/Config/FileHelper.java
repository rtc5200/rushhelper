/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.rs.rushhelper.Config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 
 * @author 
 * 
 */
public class FileHelper {
    private final File file;
    private boolean has_been_created = false;
    public FileHelper(File file){
        this.file = file;
    }
    public void adjustFile() {
        if(!file.getParentFile().exists())file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
                has_been_created = true;
            } catch (IOException ex) {
                Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public File file(){return file;}
    public boolean created(){return has_been_created;}
    public YamlConfiguration config(){return YamlConfiguration.loadConfiguration(file);}
}
