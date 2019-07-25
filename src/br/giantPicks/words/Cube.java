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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2019-5-1
 */
public class Cube implements Word {

    @Override
    public String getName() {
        return "立方挖掘";
    }

    private Config<String> lore = new Config<>("Lore", "§e[§b§l立方挖掘§e]§a: 范围: %d*%d*%d");

    @Override
    public String addLore(NbtCompound nbt) {
        int w = nbt.getInteger("w");
        int d = nbt.getInteger("d");
        int h = nbt.getInteger("h");
        return String.format(lore.getTarget(), w, d, h);
    }

    public Block getOffset(BlockFace bf, Block b, int x, int y) {
        Location loc = b.getLocation();
        switch (bf) {
            case DOWN:
            case UP:
                loc.add(x, 0, y);
                break;
            case EAST:
            case WEST:
                loc.add(0, y, x);
                break;
            case NORTH:
            case SOUTH:
                loc.add(x, y, 0);
                break;
        }
        return loc.getBlock();
    }

    @Override
    public boolean onDig(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        int w = nbt.getInteger("w") / 2;
        int d = nbt.getInteger("d");
        int h = nbt.getInteger("h") / 2;
        for (int l = 0; l < d; l++, block = block.getRelative(bf.getOppositeFace())) {
            for (int x = -w; x <= w; x++) {
                for (int y = -h; y <= h; y++) {
                    Block t = getOffset(bf, block, x, y);
                    Main.breakBlock(t, player);
                }
            }
        }
        return false;
    }

    @Override
    public String addWord(NbtCompound nbt, Material mate, String[] args) {
        if (args.length < 3) {
            return "参数不足";
        }
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            nbt.put("w", x);
            nbt.put("h", y);
            nbt.put("d", z);
        } catch (Exception e) {
            return "参数不是数字";
        }
        return "添加成功";
    }

}
