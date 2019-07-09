package eu.bebendorf.mccomputer;

import eu.bebendorf.mccomputer.api.ComputerAPI;
import eu.bebendorf.mccomputer.api.ComputerAPIPlugin;
import eu.bebendorf.mccomputer.command.ComputerCommand;
import eu.bebendorf.mccomputer.listener.ComputerBlockListener;
import eu.bebendorf.mccomputer.listener.ComputerGUIListener;
import eu.bebendorf.mccomputer.listener.ComputerPlaceListener;
import eu.bebendorf.mccomputer.listener.ScreenListener;
import eu.bebendorf.mcscreen.api.ScreenAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class MCComputer extends JavaPlugin implements ComputerAPIPlugin {

    @Getter
    private static MCComputer instance;

    @Getter
    private ComputerAPIImplementation computerManager;

    public void onEnable(){
        instance = this;
        if(!getDataFolder().exists())
            getDataFolder().mkdir();
        if(!getHostFSFolder().exists())
            getHostFSFolder().mkdir();
        if(!getComponentFolder().exists())
            getComponentFolder().mkdir();
        getCommand("computer").setExecutor(new ComputerCommand(this));
        Bukkit.getPluginManager().registerEvents(new ComputerBlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ComputerPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ComputerGUIListener(this), this);
        ScreenAPI.getInstance().addListener(new ScreenListener(this));
        computerManager = new ComputerAPIImplementation(new File(getDataFolder(), "computer.json"));
    }

    public void sendPrefixed(Player player, String message){
        player.sendMessage("§8[§cComputer§8] "+(!message.startsWith("§")?"§7":"")+message);
    }

    public ComputerAPI getAPI(){
        return computerManager;
    }

    public ItemStack getItem(){
        ItemStack item = new ItemStack(Material.BEACON, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cComputer");
        meta.setLore(new ArrayList<String>(){{
            add(" ");
            add("§7A computer that can be used");
            add("§7to control screens and redstone");
            add(" ");
        }});
        item.setItemMeta(meta);
        return item;
    }

    public static File getHostFSFolder(){
        return new File(MCComputer.getInstance().getDataFolder(), "filesystem");
    }
    public static File getComponentFolder(){
        return new File(MCComputer.getInstance().getDataFolder(), "components");
    }

}
