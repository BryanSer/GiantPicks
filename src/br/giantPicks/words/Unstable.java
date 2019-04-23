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
import org.bukkit.Location;
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
public class Unstable implements Word {

    @Override
    public String getName() {
        return "不稳定";
    }

    Config<String> lore = new Config<>("Lore", "§e[§b§l不稳定的§e]§a: 不稳定率: %.1f%%");

    @Override
    public String addLore(NbtCompound nbt) {
        return String.format(lore.getTarget(), nbt.getDouble("value") * 100);
    }

    @Override
    public boolean onDig(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        double value = nbt.getDouble("value");
        if (Math.random() < value) {
            Location loc = player.getLocation();
            player.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 1f, false, false);
            player.damage(player.getHealth() / 2);
        }
        return false;
    }

    @Override
    public String addWord(NbtCompound nbt, Material mate, String[] args) {
        if (args.length == 0) {
            return "§c参数不足";
        }

        try {
            double value = Double.parseDouble(args[0]);
            if (value > 1) {
                return "§c参数不得大于1";
            }
            nbt.put("value", value);
            return "添加成功";
        } catch (Exception e) {
            return "参数不是数字";
        }
    }
}
