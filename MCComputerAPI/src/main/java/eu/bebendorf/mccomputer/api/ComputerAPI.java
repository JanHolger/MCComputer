package eu.bebendorf.mccomputer.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public interface ComputerAPI {

    Computer getComputer(int id);
    List<Computer> getComputers();
    void remove(Computer computer, boolean dropItems);
    List<ComputerComponent> getComponents();
    ComputerComponent getComponent(UUID uuid);
    void remove(ComputerComponent component);

    static ComputerAPI getInstance(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MCScreen");
        if(plugin == null)
            return null;
        if(!(plugin instanceof ComputerAPIPlugin))
            return null;
        ComputerAPIPlugin computerAPIPlugin = (ComputerAPIPlugin) plugin;
        return computerAPIPlugin.getAPI();
    }

}
