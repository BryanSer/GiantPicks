/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */

package br.giantPicks.words;

import br.giantPicks.Config;
import br.giantPicks.Main;
import br.giantPicks.Word;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import java.util.List;
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
public class Laser implements Word{

    @Override
    public String getName() {
        return "激光钻探";
    }
    
    Config<String> lore = new Config<>("Lore","§e[§b§l激光钻探§e]§a: 激光长度: %d");

    @Override
    public String addLore( NbtCompound nbt) {
        return String.format(lore.getTarget(), nbt.getInteger("range"));
    }

    @Override
    public boolean onDig(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        bf = bf.getOppositeFace();
        int range = nbt.getInteger("range");
        for (int i = 0; i < range; i++) {
            block = block.getRelative(bf);
            Main.breakBlock(block, player);
        }
        return false;
    }

    @Override
    public String addWord(NbtCompound nbt, Material mate, String[] args) {
        if (args.length == 0) {
            return "参数不足";
        }
        try {
            int range = Integer.parseInt(args[0]);
            nbt.put("range", range);
            return "添加成功";
        } catch (Exception e) {
            return "参数不是数字";
        }
    }
    
}
