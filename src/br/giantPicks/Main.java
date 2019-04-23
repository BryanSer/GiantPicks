/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.giantPicks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import me.crafter.mc.lockettepro.LocketteProAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Bryan_lzh
 */
public class Main extends JavaPlugin implements Listener {

    public static Main Plugin;
    public static final String PREFIX = "§r§r§a§b§5§9§r";

    Map<String, BlockFace> tryDig = new HashMap<>();

    public void registerListener() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(
                new PacketAdapter(
                        PacketAdapter
                                .params()
                                .plugin(this)
                                .clientSide()
                                .gamePhase(GamePhase.PLAYING)
                                .types(PacketType.Play.Client.BLOCK_DIG)) {
            @Override
            public void onPacketReceiving(PacketEvent evt) {
                PacketContainer pc = evt.getPacket();
                if (pc.getPlayerDigTypes().getValues().get(0) == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK) {
                    EnumWrappers.Direction d = pc.getDirections().getValues().get(0);
                    BlockFace bf;
                    switch (d) {
                        case DOWN:
                            bf = BlockFace.DOWN;
                            break;
                        case UP:
                            bf = BlockFace.UP;
                            break;
                        case NORTH:
                            bf = BlockFace.NORTH;
                            break;
                        case SOUTH:
                            bf = BlockFace.SOUTH;
                            break;
                        case WEST:
                            bf = BlockFace.WEST;
                            break;
                        case EAST:
                            bf = BlockFace.EAST;
                            break;
                        default:
                            return;
                    }
                    BlockPosition pos = pc.getBlockPositionModifier().getValues().get(0);
                    tryDig.put(String.format("%d,%d,%d", pos.getX(), pos.getY(), pos.getZ()), bf);
                }
            }
        }
        );
    }

    @Override
    public void onEnable() {
        Plugin = this;
        registerListener();
        Setting.loadConfig();
        WordManager.init();
        WordManager.loadWordConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return false;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            WordManager.loadWordConfig();
            Setting.loadConfig();
            sender.sendMessage("§6重载完成");
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§6可用的词条如下: ");
            sender.sendMessage("§a[十字花刀]: 参数为挖掘半径(整数)");
            sender.sendMessage("§a[激光钻探]: 参数为挖掘深度(整数)");
            sender.sendMessage("§a[需要充能]: 无参数");
            sender.sendMessage("§a[不稳定]: 参数为爆炸概率(小数) 1为100%爆炸");
            sender.sendMessage("§a[使用次数]: 参数为使用次数(整数)");
            return true;
        }
        if (args[0].equalsIgnoreCase("add") && args.length >= 2 && sender instanceof Player) {
            Player p = (Player) sender;
            String name = args[1];
            ItemStack is = p.getInventory().getItemInMainHand();
            if (is == null) {
                p.sendMessage("§c你的手上毛都没有");
                return true;
            }
            Optional<Word> ow = WordManager.Words.stream().filter(s -> s.getName().equals(name)).findFirst();
            if (!ow.isPresent()) {
                sender.sendMessage("§c找不到这个词条");
                return true;
            }
            Word w = ow.get();
            NbtWrapper<?> nw = NbtFactory.fromItemTag(is);
            NbtCompound c = NbtFactory.asCompound(nw);
            NbtCompound gp;
            if (!c.containsKey("GiantPicks")) {
                gp = NbtFactory.ofCompound("GiantPicks");
            } else {
                gp = c.getCompound("GiantPicks");
            }
            String tar = w.addDefaultWord(gp, is.getType(), Arrays.copyOfRange(args, 2, args.length, String[].class));
            c.put("GiantPicks", gp);
            NbtFactory.setItemTag(is, c);
            p.sendMessage("§6添加结果: " + tar);
            is = setLore(is, gp);
            p.getInventory().setItemInMainHand(is);
            return true;
        }
        return false;
    }

    private static void clear(List<String> lore) {
        Iterator<String> it = lore.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (next.contains(PREFIX)) {
                it.remove();
            }
        }
    }

    public static ItemStack setLore(ItemStack is, NbtCompound gp) {
        is = is.clone();
        List<Word> words = WordManager.readWords(gp);
        ItemMeta im = is.getItemMeta();
        List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
        clear(lore);
        boolean edit = false;
        for (Word w : words) {
            NbtCompound nc = gp.getCompound(w.getName());
            int hash = nc.containsKey("hashCode") ? nc.getInteger("hashCode") : 0;
            String t = w.addLore(nc);
            t = ChatColor.translateAlternateColorCodes('&', t);
            lore.addAll(
                    Arrays
                            .stream(t.split("\\|"))
                            .map(s -> s + PREFIX)
                            .collect(Collectors.toList())
            );
            lore.add(t + PREFIX);
            edit |= t.hashCode() != hash;
            nc.put("hashCode", t.hashCode());
        }
        if (!edit) {
            return null;
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    private void dig(Block block, Player p, BlockFace bf) {
        if (!Setting.Worlds.contains(p.getWorld().getName())) {
            p.sendMessage("§c你不能在这个世界使用这把镐子");
            return;
        }
        ItemStack is = p.getInventory().getItemInMainHand();
        if (is == null) {
            return;
        }
        is = is.clone();
        NbtWrapper<?> nw = NbtFactory.fromItemTag(is);
        NbtCompound c = NbtFactory.asCompound(nw);
        if (!c.containsKey("GiantPicks")) {
            return;
        }
        NbtCompound gp = c.getCompound("GiantPicks");
        List<Word> words = WordManager.readWords(gp);
        if (words == null || words.isEmpty()) {
            return;
        }
        for (Word w : words) {
            if (!w.tryDig(block, bf, p, gp)) {
                return;
            }
        }
        for (Word w : words) {
            try {
                w.forDig(block, bf, p, gp);
            } catch (Exception e) {
                Logger.getLogger(Main.class.getName()).log(Level.WARNING, null, e);
            }
        }
        c.put("GiantPicks", gp);
        NbtFactory.setItemTag(is, c);
        ItemStack item = setLore(is, gp);
        if (item != null) {
            p.getInventory().setItemInMainHand(item);
        }

    }

    private Set<Integer> skip = new HashSet<>();

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent evt) {
        Block b = evt.getBlock();
        Location loc = b.getLocation();
        BlockFace bf = tryDig.remove(String.format("%d,%d,%d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        if (bf != null && !skip.contains(evt.getPlayer().getEntityId())) {
            skip.add(evt.getPlayer().getEntityId());
            try {
                dig(b, evt.getPlayer(), bf);
            } catch (Exception e) {
                Logger.getLogger(Main.class.getName()).log(Level.WARNING, null, e);
            }
            skip.remove(evt.getPlayer().getEntityId());
        }
    }

    public static void breakBlock(Block b, Player p) {
        if (LocketteProAPI.isProtected(b)) {
            return;
        }
        if (b.getType() == Material.BEDROCK) {
            return;
        }
        if (b.getType() == Material.MOB_SPAWNER) {
            return;
        }
        if (b.getType().toString().contains("SIGN")) {
            return;
        }
        if (b.getType() == Material.CHEST) {
            return;
        }
        b.breakNaturally(p.getInventory().getItemInMainHand());
    }

}
