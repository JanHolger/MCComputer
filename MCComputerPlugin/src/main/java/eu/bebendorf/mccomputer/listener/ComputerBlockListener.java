package eu.bebendorf.mccomputer.listener;

import eu.bebendorf.mccomputer.ComputerComponentImplementation;
import eu.bebendorf.mccomputer.ComputerImplementation;
import eu.bebendorf.mccomputer.MCComputer;
import eu.bebendorf.mccomputer.api.Computer;
import eu.bebendorf.mccomputer.api.ComputerComponent;
import eu.bebendorf.mccomputer.helper.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

@AllArgsConstructor
public class ComputerBlockListener implements Listener {

    private MCComputer plugin;

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(e.getBlock().getType() != Material.BEACON)
            return;
        Computer computer = plugin.getComputerManager().getComputer(e.getBlock().getLocation());
        if(computer == null)
            return;
        e.setCancelled(true);
        ComputerImplementation implementation = (ComputerImplementation) computer;
        if(!implementation.removalCheck(e.getPlayer())){
            return;
        }
        plugin.getComputerManager().remove(computer, true);
        MCComputer.getInstance().sendPrefixed(e.getPlayer(), "Disassembled the computer §8(§e"+computer.getId()+"§8)§7!");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if(e.getClickedBlock().getType() != Material.BEACON)
            return;
        Computer computer = plugin.getComputerManager().getComputer(e.getClickedBlock().getLocation());
        if(computer == null)
            return;
        e.setCancelled(true);
        e.setUseInteractedBlock(Event.Result.DENY);
        e.setUseItemInHand(Event.Result.DENY);
        if(e.getItem() != null && e.getItem().getType() == Material.IRON_PLATE && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasDisplayName() && e.getItem().getItemMeta().hasLore()){
            ItemMeta meta = e.getItem().getItemMeta();
            if(meta.getDisplayName().equals("§cComputer Component") && meta.getLore().size() == 4){
                UUID address = UUID.fromString(meta.getLore().get(2).substring(13));
                e.getPlayer().setItemInHand(null);
                ComputerComponent component = plugin.getComputerManager().getComponent(address);
                if(component != null){
                    ComputerImplementation computerImplementation = (ComputerImplementation) computer;
                    if(!computer.isRunning()){
                        computerImplementation.addComponent(component);
                        MCComputer.getInstance().sendPrefixed(e.getPlayer(), "The component has been installed!");
                    }else{
                        MCComputer.getInstance().sendPrefixed(e.getPlayer(), "§cThe computer needs to be shut down first!");
                    }
                }else{
                    MCComputer.getInstance().sendPrefixed(e.getPlayer(), "§cThe component was broken!");
                }
                return;
            }
        }
        Inventory gui = Bukkit.createInventory(null, 45, "§cComputer §8(§7"+computer.getId()+"§8)");
        ItemStack empty = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 8).setName(" ").build();
        for(int i=0; i<gui.getSize(); i++)
            gui.setItem(i, empty);
        gui.setItem(13, new ItemBuilder(Material.PAPER, 1).setName("§7Status").setLore(computer.isRunning() ? "§aRunning" : "§cShutdown").build());
        gui.setItem(28, new ItemBuilder(Material.INK_SACK, 1, 10).setName("§aBoot").build());
        gui.setItem(31, new ItemBuilder(Material.INK_SACK, 1, 14).setName("§eShutdown").build());
        gui.setItem(34, new ItemBuilder(Material.INK_SACK, 1, 1).setName("§cKill").build());
        e.getPlayer().openInventory(gui);
    }

}
