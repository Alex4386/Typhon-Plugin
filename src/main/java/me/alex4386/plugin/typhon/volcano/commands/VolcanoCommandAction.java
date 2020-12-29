package me.alex4386.plugin.typhon.volcano.commands;

import me.alex4386.plugin.typhon.TyphonCommand;
import me.alex4386.plugin.typhon.TyphonCommandAction;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public enum VolcanoCommandAction {
    START("start", "", "deprecated. start the maincrater"),
    STOP("stop", "", "deprecated. stop the all craters"),
    CREATE("create", "<type> <name>", "create crater, dike, magma chambers"),
    MAIN_CRATER("maincrater", "", "get main crater"),
    SUB_CRATER("subcrater", "<name>", "get subsidiary crater"),
    SUMMIT("summit", "<name>", "get summit of the volcano"),
    DIKE("dike", "<name>", "get dikes"),
    MAGMA_CHAMBER("magmachamber", "<name>", "get magmaChambers"),
    SHUTDOWN("shutdown", "<name>", "shutting down the entire volcano forcibly"),
    QUICK_COOL("quickcool", "<name>", "forcibly cooling all flowing lava"),
    SAVE("save", "", "save the volcano"),
    RELOAD("reload", "", "reload the volcano"),
    UPDATE_RATE("updaterate", "<value>", "get/set the updaterate"),

    DELETE("delete", "", "Delete this volcano");

    String cmdline;
    String usage;
    String explanation;

    VolcanoCommandAction(String cmdline, String usage, String explanation) {
        this.cmdline = cmdline;
        this.usage = usage;
        this.explanation = explanation;
    }

    public static List<String> listAll(CommandSender sender) {
        List<String> all = new ArrayList<>();

        for (VolcanoCommandAction action : VolcanoCommandAction.values()) {
            if (action.hasPermission(sender)) {
                all.add(action.getCommand());
            }
        }

        return all;
    }

    public String getManual(String label, String name) {
        return ChatColor.LIGHT_PURPLE+"/"+label+" "+ChatColor.AQUA+name+" "+ChatColor.YELLOW+this.cmdline+" "+ChatColor.GRAY+this.usage+ChatColor.RESET+" : "+this.explanation;
    }

    public static String getAllManual(CommandSender sender, String label, String name) {
        String all = "";

        for (VolcanoCommandAction action : VolcanoCommandAction.values()) {
            if (TyphonCommand.hasPermission(sender, action.getCommand())) {
                all += action.getManual(label, name)+"\n";
            }
        }

        return all;
    }

    public String getCommand() {
        return cmdline;
    }

    public boolean hasPermission(CommandSender sender) {
        return TyphonCommand.hasPermission(sender, "volcano."+this.cmdline);
    }

    public static VolcanoCommandAction getAction(String string) {
        for (VolcanoCommandAction action : VolcanoCommandAction.values()) {
            if (action.getCommand().equalsIgnoreCase(string)) {
                return action;
            }
        }
        return null;
    }
}
