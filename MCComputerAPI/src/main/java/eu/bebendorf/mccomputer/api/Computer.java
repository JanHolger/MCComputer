package eu.bebendorf.mccomputer.api;

import eu.bebendorf.mccomputer.api.execution.RuntimeEvent;
import eu.bebendorf.mccomputer.api.execution.RuntimeEventBuilder;
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
    RuntimeEventBuilder event(String eventName);
    default RuntimeEventBuilder event(RuntimeEvent event){
        return event(event.getValue());
    }

}
