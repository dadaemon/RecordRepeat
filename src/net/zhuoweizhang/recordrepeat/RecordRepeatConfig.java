/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.zhuoweizhang.recordrepeat;

import java.io.File;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Gebruiker
 */
public class RecordRepeatConfig {

    private RecordRepeatPlugin plugin;
    private FileConfiguration c;
    private String n;

    public RecordRepeatConfig(RecordRepeatPlugin plugin) {
        if (!(new File(plugin.getDataFolder(), "config.yml")).exists()) {
            plugin.writeLog("Config file defaults are being copied");
            plugin.saveDefaultConfig();
            plugin.saveConfig();
        }
        this.plugin = plugin;
        c = plugin.getConfig();
        n = plugin.getName();
        
        String version = c.getString(n + ".Version", "1.0");
        if (!version.equals(plugin.version)) {
            // Version 1.0 / 1.1 -> ...
            // * Added ...
            if (version.equals("1.0") || version.equals("1.1")) {
            }

            c.set(n + ".Version", plugin.version);
            plugin.saveConfig();
        }
    }

    private void reload() {
        plugin.reloadConfig();
        c = plugin.getConfig();
        n = plugin.getName();
    }

    public boolean getConfigDebug() {
        return c.getBoolean(n + ".Debug", false);
    }

    private void saveConfig() {
        plugin.writeDebug("Saving config and reloading!");
        plugin.saveConfig();
        reload();
    }

    private void save() {
        plugin.writeDebug("Saving config!");
        plugin.saveConfig();
    }

    public void saveJukeboxes() {
        for (RPJB jb : plugin.rJBs) {
            Block b = jb.getBlock();
            int r = jb.getRecordType().getId();

            c.set(n + ".JukeBoxes." + blockToString(b), r);
        }
        save();
    }

    public void addJukebox(Block b, Material recordType) {
        int r = recordType.getId();
        c.set(n + ".JukeBoxes." + blockToString(b), r);
        save();
    }

    private String blockToString(Block b) {
        String s = "";
        s += b.getWorld().getName() + "@" + b.getX() + "@" + b.getY() + "@" + b.getZ();
        s = s.replace("-", "_");
        return s;
    }

    private Block stringToBlock(String s) {
        s = s.replace("_", "-");
        String[] ss = s.split("@");

        World w = Bukkit.getWorld(ss[0]);
        if (w == null) {
            return null;
        }

        Block b = w.getBlockAt(Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), Integer.parseInt(ss[3]));
        return b;
    }

    public boolean removeJukebox(Block b) {
        if(c.isSet(n + ".JukeBoxes." + blockToString(b))) {
            c.set(n + ".JukeBoxes." + blockToString(b), null);
            save();
            return true;
        }
        return false;
    }

    void loadJukeBoxes() {
        if (c.isConfigurationSection(n + ".JukeBoxes")) {
            Set<String> list = c.getConfigurationSection(n + ".JukeBoxes").getKeys(false);
            if (list != null) {
                for (String jb : list) {
                    Block b = stringToBlock(jb);
                    if (b != null) {
                        plugin.addJukebox(b, Material.getMaterial(c.getInt(n + ".JukeBoxes." + jb)), true);
                    }
                }
            }

        }
    }

    boolean isNotice() {
        return c.getBoolean(n + ".Notice", true);
    }

    boolean getPlayerRepeatStatus(String name) {
        return c.getBoolean(n + ".Players." + name + ".RepeatStatus", false);
    }

    void setPlayerRepeatStatus(String name, boolean repeatStatus) {
        c.set(n + ".Players." + name + ".RepeatStatus", repeatStatus);
        this.save();
    }
}
