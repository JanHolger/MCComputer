package eu.bebendorf.mccomputer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import eu.bebendorf.mccomputer.api.Computer;
import eu.bebendorf.mccomputer.api.ComputerComponent;
import eu.bebendorf.mccomputer.api.execution.RuntimeEvent;
import eu.bebendorf.mccomputer.api.execution.RuntimeEventBuilder;
import eu.bebendorf.mccomputer.execution.ComputerRuntime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComputerImplementation implements Computer {

    @Getter
    int id;
    @Getter
    Location location;
    List<UUID> components = new ArrayList<>();
    ComputerRuntime runtime = null;

    public ComputerImplementation(int id, Location location){
        this.id = id;
        this.location = location;
    }

    public ComputerImplementation(int id, JsonObject json){
        this.id = id;
        JsonObject loc = json.getAsJsonObject("location");
        location = new Location(Bukkit.getWorld(loc.get("world").getAsString()), loc.get("x").getAsInt(), loc.get("y").getAsInt(), loc.get("z").getAsInt());
        for(JsonElement je : json.getAsJsonArray("components")){
            components.add(UUID.fromString(je.getAsString()));
        }
    }

    public List<ComputerComponent> getComponents(){
        List<ComputerComponent> componentList = new ArrayList<>();
        for(UUID address : components){
            ComputerComponent component = MCComputer.getInstance().getComputerManager().getComponent(address);
            if(component != null)
                componentList.add(component);
        }
        return componentList;
    }

    public ComputerComponent getComponent(UUID address){
        for(ComputerComponent component : getComponents()){
            if(component.getAddress().equals(address))
                return component;
        }
        return null;
    }

    public void addComponent(ComputerComponent component){
        components.add(component.getAddress());
        MCComputer.getInstance().getComputerManager().save();
    }

    public boolean isRunning(){
        if(runtime == null)
            return false;
        return runtime.isRunning();
    }

    public void kill(){
        if(runtime == null)
            return;
        runtime.kill();
    }

    public void shutdown(){
        if(runtime == null)
            return;
        runtime.event(RuntimeEvent.SHUTDOWN).dispatch();
    }

    public void boot(){
        if(runtime == null){
            runtime = new ComputerRuntime(this);
        }
        runtime.start();
    }

    public RuntimeEventBuilder event(String eventName){
        return runtime.event(eventName);
    }

    public RuntimeEventBuilder event(RuntimeEvent event){
        return event(event.getValue());
    }

    public boolean removalCheck(Player player){
        return true;
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        JsonObject loc = new JsonObject();
        loc.addProperty("world", location.getWorld().getName());
        loc.addProperty("x", location.getBlockX());
        loc.addProperty("y", location.getBlockY());
        loc.addProperty("z", location.getBlockZ());
        json.add("location", loc);
        JsonArray components = new JsonArray();
        for(ComputerComponent component : getComponents()){
            components.add(new JsonPrimitive(component.getAddress().toString()));
        }
        json.add("components", components);
        return json;
    }

}
