/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package br.giantPicks;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2019-4-22
 */
public interface Word {

    public String getName();

    public String addLore(NbtCompound nbt);

    /**
     *
     * @param block
     * @param bf
     * @param player
     * @param nbt 只会传入自己子节点
     * @return 返回是否需要更新lore
     */
    public boolean onDig(Block block, BlockFace bf, Player player, NbtCompound nbt);

    public default boolean forDig(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        if (!nbt.containsKey(this.getName())) {
            return false;
        }
        NbtCompound c = nbt.getCompound(this.getName());
        boolean t = onDig(block, bf, player, c);
        if (t) {
            nbt.put(this.getName(), c);
        }
        return t;
    }

    /**
     *
     * @param nbt 只会传入自己子节点
     * @param mate
     * @param args
     * @return 完成提示
     */
    public String addWord(NbtCompound nbt, Material mate, String[] args);

    public default String addDefaultWord(NbtCompound nbt, Material mate, String[] args) {
        if (nbt.containsKey(this.getName())) {
            return "已经存在这个词了";
        }
        NbtCompound nn = NbtFactory.ofCompound(this.getName());
        String s = addWord(nn, mate, args);
        nbt.put(this.getName(), nn);

        return s;
    }

    public default boolean isDiggable(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        return true;
    }

    public default boolean tryDig(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        if (!nbt.containsKey(this.getName())) {
            return true;
        }
        NbtCompound c = nbt.getCompound(this.getName());
        return isDiggable(block, bf, player, c);
    }

    public default List<Config<?>> getConfigs() {
        Class<? extends Word> cls = this.getClass();
        List<Config<?>> list = new ArrayList<>();
        for (Field c : getAllDeclaredFields(cls)) {
            c.setAccessible(true);
            if (c.getType().isAssignableFrom(Config.class)) {
                try {
                    list.add((Config<?>) c.get(this));
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Word.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Word.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }

    public static <T> Collection<Field> getAllDeclaredFields(Class<T> cls) {
        Class<?> t = cls;
        List<Field> f = new ArrayList<>();
        while (t != Object.class) {
            f.addAll(Arrays.asList(t.getDeclaredFields()));
            t = t.getSuperclass();
        }
        return f;
    }
}
