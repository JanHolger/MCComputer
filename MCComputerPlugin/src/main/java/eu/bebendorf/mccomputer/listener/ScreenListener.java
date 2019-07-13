package eu.bebendorf.mccomputer.listener;

import eu.bebendorf.mccomputer.MCComputer;
import eu.bebendorf.mccomputer.api.Computer;
import eu.bebendorf.mccomputer.api.ComputerComponent;
import eu.bebendorf.mccomputer.api.components.GPUComponent;
import eu.bebendorf.mccomputer.api.execution.RuntimeEvent;
import eu.bebendorf.mccomputer.components.GPUComponentImplementation;
import eu.bebendorf.mcscreen.api.Screen;
import eu.bebendorf.mcscreen.api.helper.MouseButton;
import eu.bebendorf.mcscreen.api.helper.ScreenPixel;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class ScreenListener implements eu.bebendorf.mcscreen.api.ScreenListener {

    private MCComputer plugin;

    public void onClick(Player player, Screen screen, MouseButton button, ScreenPixel pixel){
        if(player.getItemInHand() != null && player.getItemInHand().getType() == Material.IRON_PLATE && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore() && player.getItemInHand().getItemMeta().getLore().size() == 4 && player.getItemInHand().getItemMeta().getDisplayName().equals("§cComputer Component") && player.getItemInHand().getItemMeta().getLore().get(1).equals("§7Type§8: §eGPU")){
            UUID address = UUID.fromString(player.getItemInHand().getItemMeta().getLore().get(2).substring(13));
            ComputerComponent component = plugin.getComputerManager().getComponent(address);
            if(component != null){
                GPUComponent gpu = (GPUComponent) component;
                gpu.addScreen(screen);
                MCComputer.getInstance().sendPrefixed(player, "The screen has been wired to the gpu!");
            }else{
                player.setItemInHand(null);
                MCComputer.getInstance().sendPrefixed(player, "§cThe component was broken!");
            }
            return;
        }
        for(Computer computer : plugin.getComputerManager().getComputers(screen)){
            computer.event(RuntimeEvent.CLICK)
                    .param("screen", screen.getId())
                    .param("button", button.getValue())
                    .param("x", pixel.getX())
                    .param("y", pixel.getY())
                    .param("player", player.getUniqueId().toString())
                    .dispatch();
        }
    }
    public void onRemove(Screen screen) {
        for(Computer computer : MCComputer.getInstance().getComputerManager().getComputers()){
            for(ComputerComponent component : computer.getComponents()){
                if(component instanceof GPUComponentImplementation){
                    GPUComponentImplementation gpu = (GPUComponentImplementation) component;
                    if(gpu.getScreens().contains(screen)){
                        gpu.removeScreen(screen);
                    }
                }
            }
        }
    }
    public void onCreate(Screen screen) {

    }

}
