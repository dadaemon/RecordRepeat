/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.zhuoweizhang.recordrepeat;

import java.util.Calendar;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Gebruiker
 */
class RPJB {
    private Block block;
    private Material recordType;
    private Calendar repeatTime;
    
    public RPJB(Block block, Material recordType) {
        this.block = block;
        this.recordType = recordType;
        
        this.repeatTime = null;
    }

    public void setRepeatTime(Calendar repeatTime) {
        this.repeatTime = repeatTime;
    }

    public Calendar getRepeatTime() {
        return this.repeatTime;
    }
    
    public Material getRecordType() {
        return this.recordType;
    }

    public Block getBlock() {
        return this.block;
    }
}
