package eu.bebendorf.mccomputer.listener;

import eu.bebendorf.mccomputer.MCComputer;
import eu.bebendorf.mccomputer.api.Computer;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

@AllArgsConstructor
public class ComputerGUIListener implements Listener {

    private MCComputer plugin;

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null)
            return;
        if(e.getClickedInventory().getType() == null)
            return;
        if(e.getClickedInventory().getTitle() == null)
            return;
        if(e.getClickedInventory().getType() != InventoryType.CHEST)
            return;
        if(!e.getClickedInventory().getTitle().startsWith("§cComputer §8(§7"))
            return;
        e.setCancelled(true);
        e.setResult(Event.Result.DENY);
        if(e.getCurrentItem() == null)
            return;
        Player player = (Player) e.getWhoClicked();
        int computerId = Integer.parseInt(e.getClickedInventory().getTitle().substring(16, e.getClickedInventory().getTitle().length()-3));
        Computer computer = MCComputer.getInstance().getComputerManager().getComputer(computerId);
        if(computer == null){
            player.closeInventory();
            return;
        }
        if(e.getSlot() == 28){
            computer.boot();
        }
        if(e.getSlot() == 31){
            computer.shutdown();
        }
        if(e.getSlot() == 34){
            computer.kill();
        }
    }

}
