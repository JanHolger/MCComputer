package eu.bebendorf.mccomputer.command;

import eu.bebendorf.mccomputer.ComputerComponentImplementation;
import eu.bebendorf.mccomputer.ComputerImplementation;
import eu.bebendorf.mccomputer.MCComputer;
import eu.bebendorf.mccomputer.api.Computer;
import eu.bebendorf.mccomputer.api.components.FSComponent;
import eu.bebendorf.mccomputer.api.components.GPUComponent;
import eu.bebendorf.mccomputer.components.GPUComponentImplementation;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ComputerCommand implements CommandExecutor {

    private MCComputer plugin;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player player = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("computer")){
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")){
                    if(args.length == 2){
                        if(!sender.hasPermission("mccomputer.remove")){
                            plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            return true;
                        }
                        ComputerImplementation computer = (ComputerImplementation) plugin.getComputerManager().getComputer(Integer.parseInt(args[1]));
                        if(computer != null){
                            if(player.hasPermission("mccomputer.remove.admin") || computer.removalCheck(player)){
                                plugin.getAPI().remove(computer, false);
                                plugin.sendPrefixed(player, "The computer §8(§e"+computer.getId()+"§8) §7has been removed!");
                            }else{
                                plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            }
                        }else{
                            plugin.sendPrefixed(player, "§cThere was no computer found with that id!");
                        }
                        return true;
                    }
                }
                if(args[0].equalsIgnoreCase("give")){
                    if(args.length == 2){
                        if(!sender.hasPermission("mccomputer.give")){
                            plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("computer")){
                            player.getInventory().addItem(plugin.getItem());
                            plugin.sendPrefixed(player, "You received a computer!");
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("gpu")){
                            ComputerComponentImplementation component = (ComputerComponentImplementation) plugin.getComputerManager().createComponent(GPUComponent.class);
                            player.getInventory().addItem(component.getItem());
                            plugin.sendPrefixed(player, "You received a gpu!");
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("fs")){
                            ComputerComponentImplementation component = (ComputerComponentImplementation) plugin.getComputerManager().createComponent(FSComponent.class);
                            player.getInventory().addItem(component.getItem());
                            plugin.sendPrefixed(player, "You received a filesystem!");
                            return true;
                        }
                        plugin.sendPrefixed(player, "§cThis component couldn't be found!");
                        return true;
                    }
                }
                if(args[0].equalsIgnoreCase("list")){
                    if(args.length == 1){
                        if(!sender.hasPermission("mccomputer.list")){
                            plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            return true;
                        }
                        StringBuilder sb = new StringBuilder();
                        for(Computer computer : plugin.getComputerManager().getComputers()){
                            if(sb.length() > 0)
                                sb.append("§8, §e");
                            sb.append(computer.getId());
                        }
                        plugin.sendPrefixed(player, "Computer§8: §e"+sb.toString());
                        return true;
                    }
                }
                if(args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")){
                    if(args.length == 2){
                        if(!sender.hasPermission("mccomputer.teleport")){
                            plugin.sendPrefixed(player, "§cYou are not allowed to do that!");
                            return true;
                        }
                        Computer computer = plugin.getComputerManager().getComputer(Integer.parseInt(args[1]));
                        if(computer != null){
                            player.teleport(computer.getLocation().clone().add(0, 1, 0));
                            plugin.sendPrefixed(player, "You were teleported to the computer §8(§e"+computer.getId()+"§8)§7!");
                        }else{
                            plugin.sendPrefixed(player, "§cThere was no computer found with that id!");
                        }
                        return true;
                    }
                }
            }
            player.sendMessage("§e/computer §blist");
            player.sendMessage("§e/computer §bteleport|tp <id>");
            player.sendMessage("§e/computer §bdelete|remove <id>");
            player.sendMessage("§e/computer §bgive <computer|gpu|fs>");
            return true;
        }
        return false;
    }

}
