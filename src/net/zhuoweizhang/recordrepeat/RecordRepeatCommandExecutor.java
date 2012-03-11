/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.zhuoweizhang.recordrepeat;

import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author BarryG
 */
public class RecordRepeatCommandExecutor implements CommandExecutor {

    private RecordRepeatPlugin plugin;
    // Initialise executable commands for players
    // aBCDefgHijklMnopQrsTuVwxyz
    private List<String> commandList = Arrays.asList("help", "h", "r", "repeat");
    // Initialise executable commands for console
    // abcdefgHijklmnopqrStuVwxyz
    private List<String> consoleCommandList = Arrays.asList("help", "h");

    public RecordRepeatCommandExecutor(RecordRepeatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (player != null) {
            String playerName = player.getName();
            // Player commands
            if (cmd.getName().equalsIgnoreCase("rr") || cmd.getName().equalsIgnoreCase("recordrepeat")) { // If the player typed /epm then do the following...
                if (args.length > 0) {
                    if (isCommand(args[0])) {
                        //sm("Doing command " + args[0], sender);
                        if (doCommand(sender, cmd, label, args)) {
                            return true;
                        } else {
                            plugin.writeLog("Something went wrong executing command '" + cmd + "' (or command isn't implemented yet)");
                            return true;
                        }
                    }

                }
                return false;
            }
        } else {
            // Console commands
            if (cmd.getName().equalsIgnoreCase("rr") || cmd.getName().equalsIgnoreCase("recordrepeat")) {
                if (args.length > 0) {
                    if (isConsoleCommand(args[0])) {
                        if (!doCommand(sender, cmd, label, args)) {
                            sm("Something went wrong executing command '" + cmd + "' (or command isn't implemented yet)", sender);
                            return false;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isCommand(String string) {
        return this.commandList.contains(string);
    }

    private void sm(String string, CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "[" + plugin.name + "] " + ChatColor.WHITE + string);
    }

    private boolean doCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (args[0].equals("h") || args[0].equals("help")) {
            sm(plugin.pdfFile.getName() + " version " + plugin.pdfFile.getVersion(), sender);
            sm("-=-=-=-=-=-=-=-=-=-", sender);

            if (player != null) {
                // Player help
                sm("Use " + ChatColor.BLUE + "/rr repeat" + ChatColor.WHITE + " to switch between repeat mode", sender);
            } else {
                // Console help
                sm("No console commands available.", sender);
            }

            return true;
        } else if (args[0].equals("r") || args[0].equals("repeat")) {

            if (player != null) {
                if (player.hasPermission("recordrepeat.use")) {
                    boolean rs = plugin.c.getPlayerRepeatStatus(player.getName());
                    if (rs) {
                        sm("Newly placed discs " + ChatColor.RED + "won't" + ChatColor.WHITE + " repeat.", sender);
                    } else {
                        sm("Newly placed discs " + ChatColor.GREEN + "will" + ChatColor.WHITE + " repeat.", sender);
                    }
                    plugin.c.setPlayerRepeatStatus(player.getName(), !rs);
                } else {
                    sm(ChatColor.RED + "You don't have permission to this command!", sender);
                }
            } else {
                return false;
            }

            return true;
        }

        return false;
    }

    private String getCommandList() {
        String itemList = "";
        for (String item : commandList) {
            if (item.length() > 1) {
                itemList = itemList + item + ", ";
            }
        }
        itemList = itemList.substring(0, itemList.length() - 2);
        return itemList;
    }

    private String getConsoleCommandList() {
        String itemList = "";
        for (String item : consoleCommandList) {
            if (item.length() > 1) {
                itemList = itemList + item + ", ";
            }
        }
        itemList = itemList.substring(0, itemList.length() - 2);
        return itemList;
    }

    private boolean isConsoleCommand(String string) {
        return this.consoleCommandList.contains(string);
    }
}
