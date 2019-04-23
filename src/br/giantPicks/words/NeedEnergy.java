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
public class NeedEnergy implements Word {

    @Override
    public String getName() {
        return "需要充能";
    }

    private Config<String> lore = new Config<>("Lore", "§e[§b§l需要充能§e]");

    @Override
    public String addLore(NbtCompound nbt) {
        return lore.getTarget();
    }

    @Override
    public boolean onDig(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        return false;
    }

    @Override
    public String addWord(NbtCompound nbt, Material mate, String[] args) {
        return "添加成功";
    }

    @Override
    public boolean isDiggable(Block block, BlockFace bf, Player player, NbtCompound nbt) {
        Location loc = player.getLocation().clone();
        loc.setY(loc.getBlockY() - 1);
        if(loc.getBlock().getType() == Material.REDSTONE_BLOCK){
            return true;
        }
        player.sendMessage("§6你的脚下必须是红石块才能为这个镐子充能");
        return false;
    }

}
