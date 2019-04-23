/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */

package br.giantPicks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2019-4-22
 */
public class Setting {
    public static List<String> Worlds = new ArrayList<>();
    
    public static void loadConfig(){
        Worlds.clear();
        File f = new File(Main.Plugin.getDataFolder(),"config.yml");
        if(!f.exists()){
            Main.Plugin.saveDefaultConfig();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
        Worlds.addAll(config.getStringList("Worlds"));
    }
}
