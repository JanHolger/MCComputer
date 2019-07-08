package eu.bebendorf.mccomputer.api;

import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public interface Computer {

    int getId();
    Location getLocation();
    List<ComputerComponent> getComponents();
    ComputerComponent getComponent(UUID address);
    boolean isRunning();
    void kill();
    void boot();
    void shutdown();

}
