/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package br.giantPicks;

import br.giantPicks.words.*;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2019-4-22
 */
public class WordManager {

    public static List<Word> Words = new ArrayList<>();

    public static void init() {
        Words.add(new Cross());
        Words.add(new Laser());
        Words.add(new NeedEnergy());
        Words.add(new Unstable());
        Words.add(new UseTime());
        Words.add(new Cube());
    }

    public static void loadWordConfig() {
        File f = new File(Main.Plugin.getDataFolder(), "WordConfig.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(WordManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
        boolean edit = false;
        for (Word w : Words) {
            ConfigurationSection cs;
            if (config.contains(w.getName())) {
                cs = config.getConfigurationSection(w.getName());
            } else {
                cs = config.createSection(w.getName());
                edit = true;
            }
            for (Config c : w.getConfigs()) {
                if (cs.contains(c.getName())) {
                    c.setTarget(cs.get(c.getName()));
                } else {
                    cs.set(c.getName(), c.getTarget());
                    edit = true;
                }
            }
        }
        if (edit) {
            try {
                config.save(f);
            } catch (IOException ex) {
                Logger.getLogger(WordManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static List<Word> readWords(NbtCompound gp) {
            List<Word> list = new ArrayList<>();
            for (Word w : Words) {
                if(gp.containsKey(w.getName())){
                    list.add(w);
                }
            }
            return list;
    }
}
