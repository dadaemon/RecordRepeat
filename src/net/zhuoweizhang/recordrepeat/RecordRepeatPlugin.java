package net.zhuoweizhang.recordrepeat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RecordRepeatPlugin extends JavaPlugin {

    public RecordRepeatPlayerListener playerListener;
    public PluginDescriptionFile pdfFile;
    private String name;
    public RecordRepeatConfig c;
    private String version;
    public static final Logger logger = Logger.getLogger("Minecraft");
    public List<RPJB> rJBs; // registeredJukeboxes;
    /** Length of the record Cat, in seconds. */
    public Map<Integer, Integer> songs = new HashMap<Integer, Integer>();
    private RRTimer rrTimer;
    private int asyncSchedule;

    @Override
    public void onEnable() {
        pdfFile = this.getDescription();
        name = pdfFile.getName();
        version = pdfFile.getVersion();
        
        // Read config
        c = new RecordRepeatConfig(this);
        
        if(c.getConfigDebug()) {
            writeLog("Debug enabled.");
        }

        // Read songs
        // TODO: Enable/disable songs from config
        songs.put(2256, (2 * 60) + 58); // 13         2:58
        songs.put(2257, (3 * 60) + 5);  // Cat        3:05
        songs.put(2258, (5 * 60) + 45); // Block      5:45
        songs.put(2259, (3 * 60) + 5);  // Chirp      3:05
        songs.put(2260, (2 * 60) + 54); // Far        2:54
        songs.put(2261, (3 * 60) + 17); // Mall       3:17
        songs.put(2262, (1 * 60) + 36); // Mellohi    1:36
        songs.put(2263, (2 * 60) + 30); // Stal       2:30
        songs.put(2264, (3 * 60) + 8);  // Strad      3:08
        songs.put(2265, (4 * 60) + 11); // Ward       4:11
        songs.put(2266, (1 * 60) + 11); // 11         1:11

        // Create and read jukeboxes from config
        rJBs = new ArrayList<RPJB>();
        c.loadJukeBoxes();
        
        playerListener = new RecordRepeatPlayerListener(this);

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(playerListener, this);

        // Load event timer
        rrTimer = new RRTimer(this);
        asyncSchedule = getServer().getScheduler().scheduleAsyncRepeatingTask(this, rrTimer, 80L, 20L);


        writeLog("== " + name + " " + version + " ENABLED ==");
    }

    @Override
    public void onDisable() {
        writeLog("== " + name + " " + version + " DISABLING ==");
        getServer().getScheduler().cancelTask(asyncSchedule);
        writeLog("== " + name + " " + version + " DISABLED ==");
    }

    public void writeLog(String text) {
        this.logger.info("[" + name + " " + version + "] " + text);
    }

    public void writeDebug(String text) {
        if (c.getConfigDebug()) {
            this.logger.info("[" + name + " DEBUG] " + text);
        }
    }

    public void addJukebox(Block block, Material recordType) {
        addJukebox(block, recordType, false);
    }

    public void addJukebox(Block block, Material recordType, Boolean forcePlay) {
        RPJB newBox = new RPJB(block, recordType);

        Calendar newDate = Calendar.getInstance();
        if (!forcePlay) {
            newDate.add(Calendar.SECOND, getRepeatTime(recordType));
        }

        newBox.setRepeatTime(newDate);

        writeDebug("Added repeat (recordType: " + recordType.toString() + " repeat@ " + newDate.getTime() + ")");
        rJBs.add(newBox);

        c.addJukebox(block, recordType);
    }

    public void removeJukebox(Block block) {
        if (!rJBs.isEmpty()) {
            for (RPJB jb : rJBs) {
                if (jb.getBlock().equals(block)) {
                    writeDebug("Removed repeat");
                    rJBs.remove(jb);
                    break;
                }
            }
        }
        c.removeJukebox(block);
    }

    private int getRepeatTime(Material recordType) {
        Integer time = songs.get(recordType.getId());
        if (time != null) {
            return time;
        }
        return 5;
    }

    public void checkRepeats() {
        //writeDebug("Checking for repeats...");
        if (!rJBs.isEmpty()) {
            //writeDebug("Not empty...");
            Calendar thisDate = Calendar.getInstance();
            for (RPJB jb : rJBs) {
                //writeDebug("Checking " + jb.getRepeatTime().getTime() + " now: " + Calendar.getInstance().getTime());
                if (jb.getRepeatTime().before(thisDate)) {
                    writeDebug("REPEAT!");

                    if (!jb.getBlock().getType().equals(Material.JUKEBOX)) {
                        rJBs.remove(jb);
                        break;
                    }

                    Jukebox jukebox = (Jukebox) jb.getBlock().getState();
                    jukebox.setPlaying(jb.getRecordType());
                    jb.getBlock().getWorld().playEffect(jb.getBlock().getLocation(), Effect.RECORD_PLAY, jb.getRecordType().getId(), 128);

                    Calendar newDate = Calendar.getInstance();
                    newDate.add(Calendar.SECOND, getRepeatTime(jb.getRecordType()));
                    jb.setRepeatTime(newDate);
                }
            }
        }
    }
}
