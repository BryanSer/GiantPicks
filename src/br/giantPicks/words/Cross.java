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
import java.util.Arrays;
import java.util.List;
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
public class Cross implements Word {

    List<BlockFace> faces = Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST);

    @Override
    public String getName() {
        return "十字花刀";
    }
    
    private Config<String> lore = new Config<>("Lore","§e[§b§l十字花刀§e]§a: 半径: %d");

    @Override
    public String addLore(NbtCompound nbt) {
        return String.format(this.lore.getTarget(), nbt.getInteger("range"));
    }

    @Override
    public boolean onDig(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        int range = nbt.getInteger("range");
        BlockFace of = bf.getOppositeFace();
        for (BlockFace f : faces) {
            if (f == of || f == bf) {
                continue;
            }
            Block target = block;
            for (int i = 0; i < range; i++) {
                target = target.getRelative(f);
                Main.breakBlock(target, player);
            }
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
        } catch (Exception e) {
            return "参数不是数字";
        }
        return "添加成功";
    }

}
