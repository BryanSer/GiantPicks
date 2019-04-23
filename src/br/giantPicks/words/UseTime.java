/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package br.giantPicks.words;

import br.giantPicks.Config;
import br.giantPicks.Word;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2019-4-23
 */
public class UseTime implements Word {

    @Override
    public String getName() {
        return "使用次数";
    }

    private Config<String> lore = new Config<>("lore", "§e[§b§l使用次数§e]§a: %d");

    @Override
    public String addLore(NbtCompound nbt) {
        return String.format(lore.getTarget(), nbt.getInteger("times"));
    }

    @Override
    public boolean onDig(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        int t = nbt.getInteger("times") - 1;
        nbt.put("times", t);
        return true;
    }

    @Override
    public String addWord(NbtCompound nbt, Material mate, String[] args) {
        if (args.length == 0) {
            return "参数不足";
        }
        try {
            int range = Integer.parseInt(args[0]);
            nbt.put("times", range);
        } catch (Exception e) {
            return "参数不是数字";
        }
        return "添加成功";
    }

    @Override
    public boolean isDiggable(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        int times = nbt.getInteger("times");
        if (times <= 0) {
            return false;
        }
        return true;
    }

}
