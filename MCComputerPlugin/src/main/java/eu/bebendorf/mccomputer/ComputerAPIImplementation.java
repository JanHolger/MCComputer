package eu.bebendorf.mccomputer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.bebendorf.mccomputer.api.Computer;
import eu.bebendorf.mccomputer.api.ComputerAPI;
import eu.bebendorf.mccomputer.api.ComputerComponent;
import eu.bebendorf.mccomputer.api.components.FSComponent;
import eu.bebendorf.mccomputer.api.components.GPUComponent;
import eu.bebendorf.mccomputer.components.FSComponentImplementation;
import eu.bebendorf.mccomputer.components.GPUComponentImplementation;
import eu.bebendorf.mcscreen.api.Screen;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ComputerAPIImplementation implements ComputerAPI {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private List<ComputerImplementation> computers = new ArrayList<>();
    @Getter
    private List<ComputerComponent> components = new ArrayList<>();
    private File saveFile;

    public ComputerAPIImplementation(){
        this(null);
    }

    public ComputerAPIImplementation(File saveFile){
        for(File f : MCComputer.getComponentFolder().listFiles()){
            if(f.isDirectory())
                continue;
            if(!f.getName().endsWith(".json"))
                continue;
            UUID address = UUID.fromString(f.getName().substring(0, f.getName().length()-5));
            ComputerComponentImplementation component = ComputerComponentImplementation.load(address);
            if(component != null){
                components.add(component);
            }
        }
        this.load(saveFile);
    }

    public Computer getComputer(int id) {
        for(Computer computer : computers)
            if(computer.getId() == id)
                return computer;
        return null;
    }

    public Computer getComputer(Location location){
        for(Computer computer : getComputers()){
            if(!computer.getLocation().getWorld().equals(location.getWorld()))
                continue;
            if(computer.getLocation().getBlockX() != location.getBlockX())
                continue;
            if(computer.getLocation().getBlockY() != location.getBlockY())
                continue;
            if(computer.getLocation().getBlockZ() != location.getBlockZ())
                continue;
            return computer;
        }
        return null;
    }

    public List<Computer> getComputers() {
        return new ArrayList<>(computers);
    }

    public ComputerComponent getComponent(UUID address){
        for(ComputerComponent component : getComponents()){
            if(component.getAddress().equals(address))
                return component;
        }
        return null;
    }

    public void remove(Computer computer, boolean dropItems){
        computer.getLocation().getBlock().setType(Material.AIR);
        if(dropItems){
            Location dropLocation = computer.getLocation().clone().add(0.5, 0.5, 0.5);
            dropLocation.getWorld().dropItem(dropLocation, MCComputer.getInstance().getItem());
            for(ComputerComponent c : computer.getComponents()){
                ComputerComponentImplementation component = (ComputerComponentImplementation) c;
                dropLocation.getWorld().dropItem(dropLocation, component.getItem());
            }
        }
        computer.shutdown();
        computer.kill();
        computers.remove(computer);
        save(saveFile);
    }

    public ComputerImplementation createComputer(Location location){
        ComputerImplementation computer = new ComputerImplementation(getFreeId(), location);
        computers.add(computer);
        save(saveFile);
        return computer;
    }

    public ComputerComponent createComponent(Class<? extends ComputerComponent> clazz){
        ComputerComponentImplementation component = null;
        if(clazz.equals(GPUComponent.class)){
            component = new GPUComponentImplementation();
        }
        if(clazz.equals(FSComponent.class)){
            component = new FSComponentImplementation();
        }
        if(component != null){
            components.add(component);
            component.save();
        }
        return component;
    }

    public void remove(ComputerComponent component){
        components.remove(component);
    }

    private int getFreeId(){
        for(int i=0; i < Integer.MAX_VALUE; i++){
            if(getComputer(i) == null)
                return i;
        }
        return -1;
    }

    public void load(File saveFile){
        this.saveFile = saveFile;
        computers.clear();
        if(saveFile == null)
            return;
        if(!saveFile.exists()){
            this.save(saveFile);
            return;
        }
        JsonObject json = gson.fromJson(readFile(saveFile), JsonObject.class);
        for(Map.Entry<String, JsonElement> entry : json.entrySet()){
            computers.add(new ComputerImplementation(Integer.parseInt(entry.getKey()), entry.getValue().getAsJsonObject()));
        }
    }

    public void save(){
        save(saveFile);
    }

    public void save(File saveFile){
        JsonObject json = new JsonObject();
        for(ComputerImplementation computer : computers){
            json.add(String.valueOf(computer.getId()), computer.toJson());
        }
        writeFile(saveFile, gson.toJson(json));
    }

    private String readFile(File file){
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            while (fis.available() > 0){
                byte[] data = new byte[Math.min(fis.available(), 4096)];
                fis.read(data);
                sb.append(new String(data, StandardCharsets.UTF_8));
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void writeFile(File file, String content){
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Computer> getComputers(Screen screen){
        List<Computer> computers = new ArrayList<>();
        for(Computer computer : MCComputer.getInstance().getComputerManager().getComputers()){
            for(ComputerComponent component : computer.getComponents()){
                if(component instanceof GPUComponentImplementation){
                    GPUComponentImplementation gpu = (GPUComponentImplementation) component;
                    if(gpu.getScreens().contains(screen)){
                        computers.add(computer);
                        break;
                    }
                }
            }
        }
        return computers;
    }


}
