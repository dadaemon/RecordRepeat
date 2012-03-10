/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.zhuoweizhang.recordrepeat;

/**
 *
 * @author barryg
 */
public class RRTimer implements Runnable {
    private RecordRepeatPlugin plugin;
    
    public RRTimer(RecordRepeatPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        //plugin.writeDebug("BTVanishTimer FIRE!");
        plugin.checkRepeats();
    }
}
