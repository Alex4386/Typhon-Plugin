package me.alex4386.plugin.typhon.volcano.commands;

import me.alex4386.plugin.typhon.TyphonUtils;
import me.alex4386.plugin.typhon.volcano.crater.VolcanoCrater;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VolcanoCommandUtils {
    public static void findSummitAndSendToSender(CommandSender sender, VolcanoCrater crater) {
        Block summitBlock = crater.getSummitBlock();

        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            String directions = TyphonUtils.getDirections(player.getLocation(), summitBlock.getLocation());

            int toClimb = summitBlock.getY() - player.getLocation().getBlockY();

            sender.sendMessage("Directions: "+directions);
            sender.sendMessage("climb: "+toClimb+" blocks");

        }

        sender.sendMessage("Located @ "+TyphonUtils.blockLocationTostring(summitBlock)+" on crater "+crater.getName());
    }

    public static String getSubmenuName(String submenuString, String[] args) {
        // vol name mainCrater command
        // -1  0    1          2

        // vol name subCrater name command
        // -1  0    1         2    3

        List<String> parsedArgs = new ArrayList<>(Arrays.asList(args));
        parsedArgs.remove(0);

        if (parsedArgs.get(0).equalsIgnoreCase(submenuString)) {
            return parsedArgs.get(1);
        }

        return null;
    }

    public static String[] parseSubmenuCommand(String submenuString, String[] args) {
        // vol name mainCrater command
        // -1  0    1          2

        // vol name subCrater name command
        // -1  0    1         2    3

        List<String> parsedArgs = new ArrayList<>(Arrays.asList(args));
        parsedArgs.remove(0);

        if (parsedArgs.get(0).equalsIgnoreCase(submenuString)) {
            parsedArgs.remove(0);
        }
        parsedArgs.remove(0);

        return parsedArgs.toArray(new String[parsedArgs.size()]);
    }

}


