package eu.bebendorf.mccomputer.listener;

import eu.bebendorf.mccomputer.ComputerImplementation;
import eu.bebendorf.mccomputer.MCComputer;
import eu.bebendorf.mccomputer.api.Computer;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

@AllArgsConstructor
public class ComputerPlaceListener implements Listener {

    private MCComputer plugin;

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(e.isCancelled())
            return;
        if(!e.getItemInHand().hasItemMeta())
            return;
        ItemMeta meta = e.getItemInHand().getItemMeta();
        if(!meta.hasDisplayName())
            return;
        if(e.getItemInHand().getType() == Material.BEACON && meta.getDisplayName().equals("§cComputer")){
            Computer computer = plugin.getComputerManager().createComputer(e.getBlock().getLocation());
            MCComputer.getInstance().sendPrefixed(e.getPlayer(), "Assembled the computer §8(§e"+computer.getId()+"§8)§7!");
        }
        if(e.getItemInHand().getType() == Material.IRON_PLATE && meta.getDisplayName().equals("§cComputer Component")){
            e.setCancelled(true);
            e.setBuild(false);
        }
    }

}
