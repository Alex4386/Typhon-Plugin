package me.alex4386.plugin.typhon.volcano.commands;

import me.alex4386.plugin.typhon.TyphonCommand;
import me.alex4386.plugin.typhon.TyphonUtils;
import me.alex4386.plugin.typhon.volcano.crater.VolcanoCrater;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class VolcanoCraterCommand {
    VolcanoCrater crater;
    boolean isMainCrater;

    public VolcanoCraterCommand(VolcanoCrater crater) {
        this.crater = crater;
        this.isMainCrater = crater.equals(crater.volcano.mainCrater);
    }

    public VolcanoCraterCommand(VolcanoCrater crater, boolean isMainCrater) {
        this.crater = crater;
        this.isMainCrater = isMainCrater;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        // vol name mainCrater command
        // -1  0    1          2

        // vol name subCrater name command
        // -1  0    1         2    3

        int baseOffset = 2 + (isMainCrater ? 0 : 1);

        if (args.length > baseOffset) {
            String operationName = args[baseOffset];

            if (args.length == baseOffset + 1) {
                return TyphonCommand.search(operationName, VolcanoCraterCommandAction.listAll(sender));
            } else if (args.length >= baseOffset + 2) {
                VolcanoCraterCommandAction action = VolcanoCraterCommandAction.getAction(operationName);

                if (action != null) {
                    if (action == VolcanoCraterCommandAction.ERUPT || action == VolcanoCraterCommandAction.LAVA_FLOW) {
                        if (args.length == baseOffset + 2) {
                            String[] res = {"start", "stop", "now"};
                            return Arrays.asList(res);
                        } else if (args.length == baseOffset + 3) {
                            if (args[baseOffset + 1].toLowerCase().equals("now")) {
                                String[] res = {"<? count>"};
                                return Arrays.asList(res);
                            }
                        }
                    } else if (action == VolcanoCraterCommandAction.CONFIG) {
                        if (args.length == baseOffset + 2) {
                            String[] configNodes = {
                                    "lavaflow:delay",
                                    "lavaflow:flowed",
                                    "bombs:launchPower:min",
                                    "bombs:launchPower:max",
                                    "bombs:explosionPower:min",
                                    "bombs:explosionPower:max",
                                    "bombs:radius:min",
                                    "bombs:radius:max",
                                    "bombs:delay",
                                    "erupt:delay",
                                    "erupt:bombs:min",
                                    "erupt:bombs:max",
                                    "erupt:explosion:size",
                                    "erupt:explosion:damagingSize",
                                    "crater:radius"
                            };

                            return TyphonCommand.search(args[baseOffset + 1], Arrays.asList(configNodes));

                        } else if (args.length == baseOffset + 3) {
                            String[] res = { "<?value>" };
                            return Arrays.asList(res);
                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String[] newArgs = VolcanoCommandUtils.parseSubmenuCommand("subCrater", args);

        String operationName = "";
        operationName = newArgs.length > 0 ? newArgs[0] : "info";
        VolcanoCraterCommandAction action = VolcanoCraterCommandAction.getAction(operationName);

        VolcanoMessage msg = new VolcanoMessage(this.crater.volcano, sender);

        if (!action.hasPermission(sender)) {
            msg.error("You don't have enough permission to run "+action.getCommand());
            return true;
        }

        switch (action) {
            case START:
                crater.start();
                msg.info("Crater "+crater.getName()+" is now started!");
                break;
            case STOP:
                crater.stop();
                msg.info("Crater "+crater.getName()+" is now stopped!");
                break;
            case ERUPT:
                if (newArgs.length == 1) {
                    msg.info("Crater "+crater.name+" is "+(crater.isErupting() ? "" : "not ")+"erupting now!");
                } else {
                    if (newArgs[1].equalsIgnoreCase("start")) {

                        if (crater.isErupting()) {
                            msg.warn("Crater "+crater.getName()+" is already erupting!");
                            break;
                        }
                        crater.startErupting();
                        msg.info("Crater "+crater.getName()+" is now erupting!");

                    } else if (newArgs[1].equalsIgnoreCase("stop")) {
                        if (crater.isErupting()) {
                            msg.warn("Crater "+crater.getName()+" is not erupting!");
                            break;
                        }
                        crater.stopErupting();
                        msg.info("Crater "+crater.getName()+" is now stopped erupting!");

                    } else if (newArgs[1].equalsIgnoreCase("now")) {
                        if (newArgs.length == 3) {
                            int bombCount = Integer.parseInt(newArgs[2]);
                            crater.erupt(bombCount);
                            msg.info("Crater "+crater.getName()+" is erupting "+bombCount+" bombs now!");
                        } else {
                            crater.erupt();
                            msg.info("Crater "+crater.getName()+" is erupting now!");
                        }
                    }
                }
                break;
            case SUMMIT:
                sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"[Crater Summit] "+ChatColor.GOLD+"Summit of crater "+crater.name);

                VolcanoCommandUtils.findSummitAndSendToSender(sender, this.crater);
                break;
            case HELP:
                sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"[Typhon Plugin] "+ChatColor.GOLD+"Volcano Crater Command Manual");
                sender.sendMessage(VolcanoCraterCommandAction.getAllManual(sender, label, this.crater.volcano.name, crater.name));
                break;
            case DELETE:
                if (crater.isMainCrater()) {
                    msg.error("Since this crater is main crater, you should delete the entire volcano. instead of deleting this.");
                } else {
                    crater.stop();
                    crater.shutdown();
                    crater.volcano.subCraters.remove(crater.name);
                    msg.info("Crater "+crater.name+" has been deleted!");
                }
                break;
            case LAVA_FLOW:
                if (newArgs.length == 1) {
                    msg.info("Crater "+crater.name+" is "+(crater.isErupting() ? "" : "not ")+"flowing lava right now!");
                } else {
                    if (newArgs[1].equalsIgnoreCase("start")) {

                        if (crater.isErupting()) {
                            msg.warn("Crater "+crater.getName()+" is already flowing lava!");
                            break;
                        }
                        crater.startFlowingLava();
                        msg.info("Crater "+crater.getName()+" is now flowing lava!");

                    } else if (newArgs[1].equalsIgnoreCase("stop")) {
                        if (crater.isErupting()) {
                            msg.warn("Crater "+crater.getName()+" is not flowing lava!");
                            break;
                        }
                        crater.stopFlowingLava();
                        msg.info("Crater "+crater.getName()+" is now stopped flowing lava!");

                    } else if (newArgs[1].equalsIgnoreCase("now")) {
                        if (newArgs.length == 3) {
                            int flowAmount = Integer.parseInt(newArgs[2]);
                            for (int i = 0; i < flowAmount; i++) {
                                crater.lavaFlow.flowLava();
                            }
                            msg.info("Crater "+crater.getName()+" is flowing "+flowAmount+" blocks of lava now!");
                        } else {
                            crater.lavaFlow.flowLava();
                            msg.info("Crater "+crater.getName()+" is erupting now!");
                        }
                    }
                }
                break;
            case QUICK_COOL:
                crater.lavaFlow.cooldownAll();
                msg.info("Cooled down all lava from crater "+crater.getName());
                break;
            case CONFIG:
                if (newArgs[1].equalsIgnoreCase("lavaflow:delay")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.lavaFlow.settings.delayFlowed = Integer.parseInt(newArgs[2]);
                        msg.info("lavaflow:delay - "+ crater.lavaFlow.settings.delayFlowed+" ticks");
                    }
                } else if (newArgs[1].equalsIgnoreCase("lavaflow:flowed")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.lavaFlow.settings.flowed = Integer.parseInt(newArgs[2]);
                        msg.info("lavaflow:flowed - "+ crater.lavaFlow.settings.flowed+" ticks");
                    }
                } else if (newArgs[1].equalsIgnoreCase("bombs:launchPower:min")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.bombs.minBombLaunchPower = Float.parseFloat(newArgs[2]);
                        msg.info("bombs:launchPower:min - "+ crater.bombs.minBombLaunchPower);
                    }
                } else if (newArgs[1].equalsIgnoreCase("bombs:launchPower:max")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.bombs.maxBombLaunchPower = Float.parseFloat(newArgs[2]);
                        msg.info("bombs:launchPower:max - "+ crater.bombs.maxBombLaunchPower);
                    }
                } else if (newArgs[1].equalsIgnoreCase("bombs:explosionPower:min")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.bombs.minBombPower = Float.parseFloat(newArgs[2]);
                        msg.info("bombs:explosionPower:min - "+ crater.bombs.minBombPower);
                    }
                } else if (newArgs[1].equalsIgnoreCase("bombs:explosionPower:max")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.bombs.maxBombPower = Float.parseFloat(newArgs[2]);
                        msg.info("bombs:explosionPower:max - "+ crater.bombs.maxBombPower);
                    }
                } else if (newArgs[1].equalsIgnoreCase("bombs:radius:min")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.bombs.minBombRadius = Integer.parseInt(newArgs[2]);
                        msg.info("bombs:radius:min - "+ crater.bombs.minBombRadius);
                    }
                } else if (newArgs[1].equalsIgnoreCase("bombs:radius:max")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.bombs.maxBombRadius = Integer.parseInt(newArgs[2]);
                        msg.info("bombs:radius:max - "+ crater.bombs.maxBombRadius);
                    }
                } else if (newArgs[1].equalsIgnoreCase("bombs:delay")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.bombs.bombDelay = Integer.parseInt(newArgs[2]);
                        msg.info("bombs:delay - "+ crater.bombs.bombDelay);
                    }
                } else if (newArgs[1].equalsIgnoreCase("erupt:delay")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.erupt.settings.explosionDelay = Integer.parseInt(newArgs[2]);
                        msg.info("erupt:delay - "+ crater.erupt.settings.explosionDelay);
                    }
                } else if (newArgs[1].equalsIgnoreCase("erupt:bombs:min")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.erupt.settings.minBombCount = Integer.parseInt(newArgs[2]);
                        msg.info("erupt:bombs:min - "+ crater.erupt.settings.minBombCount);
                    }
                } else if (newArgs[1].equalsIgnoreCase("erupt:bombs:max")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.erupt.settings.maxBombCount = Integer.parseInt(newArgs[2]);
                        msg.info("erupt:bombs:max - "+ crater.erupt.settings.maxBombCount);
                    }
                } else if (newArgs[1].equalsIgnoreCase("erupt:explosion:size")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.erupt.settings.explosionSize = Integer.parseInt(newArgs[2]);
                        msg.info("erupt:explosion:size - "+ crater.erupt.settings.explosionSize);
                    }
                } else if (newArgs[1].equalsIgnoreCase("erupt:explosion:damagingSize")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.erupt.settings.damagingExplosionSize = Integer.parseInt(newArgs[2]);
                        msg.info("erupt:explosion:damagingSize - "+ crater.erupt.settings.damagingExplosionSize);
                    }
                } else if (newArgs[1].equalsIgnoreCase("crater:radius")) {
                    if (newArgs.length >= 2) {
                        if (newArgs.length == 3) crater.craterRadius = Integer.parseInt(newArgs[2]);
                        msg.info("crater:radius - "+ crater.craterRadius);
                    }
                } else {
                    msg.error("Invalid config node!");
                }

                crater.volcano.trySave();

                break;
            case INFO:
            default:
                sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"[Typhon Plugin] "+ChatColor.GOLD+"Volcano Crater Info");
                msg.info("Location: "+ TyphonUtils.blockLocationTostring(crater.location.getBlock()));
                msg.info("Summit  : "+ TyphonUtils.blockLocationTostring(crater.getSummitBlock()));
                msg.info("LavaFlow: "+ crater.isFlowingLava());
                msg.info("Erupting: "+ crater.isErupting());
                msg.info("Radius  : "+ crater.getRadius());

                sender.sendMessage("type \"/"+label+" "+ crater.volcano.name+" "+args[1]+(args.length > 2 ? " "+args[2]:"")+" help\" for more commands.");

                break;
        }
        return true;
    }

}