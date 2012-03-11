package net.zhuoweizhang.recordrepeat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RecordRepeatPlayerListener implements Listener {

    public RecordRepeatPlugin plugin;

    public RecordRepeatPlayerListener(RecordRepeatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.isCancelled() && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.hasBlock() && event.getClickedBlock().getType().equals(Material.JUKEBOX)) { //jukebox
                Player p = event.getPlayer();
                Block block = event.getClickedBlock();
                Jukebox jb = (Jukebox) block.getState();
                ItemStack item = event.getItem();
                if (jb.getPlaying() != null && jb.getPlaying() != Material.AIR) {
                    if (p.hasPermission("recordrepeat.use")) {
                        plugin.writeDebug("eject");
                        if (plugin.removeJukebox(block)) {
                            if (plugin.c.isNotice()) {
                                p.sendMessage(ChatColor.BLUE + "Repeating record has been " + ChatColor.RED + "removed" + ChatColor.BLUE + ".");
                            }
                        }
                    }
                } else if (plugin.songs.containsKey(item.getTypeId())) {
                    if (p.hasPermission("recordrepeat.use")) {
                        if (!plugin.c.getPlayerRepeatStatus(p.getName())) {
                            if (plugin.c.isNotice()) {
                                p.sendMessage(ChatColor.BLUE + "Record " + ChatColor.RED + "won't" + ChatColor.BLUE + " be repeated.");
                            }
                        } else {
                            plugin.addJukebox(block, item.getType());
                            if (plugin.c.isNotice()) {
                                p.sendMessage(ChatColor.BLUE + "Repeating record has been " + ChatColor.GREEN + "added" + ChatColor.BLUE + ".");
                            }
                        }
                    } else {
                        if (plugin.c.isNotice()) {
                            p.sendMessage(ChatColor.BLUE + "Your record " + ChatColor.RED + "won't " + ChatColor.BLUE + "be repeated because of insufficient rights.");
                        }
                    }
                }
            }
        }
    }
}
