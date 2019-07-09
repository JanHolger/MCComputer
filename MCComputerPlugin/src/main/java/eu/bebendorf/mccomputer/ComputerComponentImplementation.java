package eu.bebendorf.mccomputer;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import eu.bebendorf.mccomputer.api.ComputerComponent;
import eu.bebendorf.mccomputer.components.FSComponentImplementation;
import eu.bebendorf.mccomputer.components.GPUComponentImplementation;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public abstract class ComputerComponentImplementation implements ComputerComponent {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ComputerComponentImplementation(UUID address){
        if(address == null){
            this.address = UUID.randomUUID();
        }else{
            this.address = address;
        }
    }

    @Getter
    private UUID address;
    public abstract JsonObject toJson();
    public void remove(){
        if(getFile().exists())
            getFile().delete();
    }
    public File getFile(){
        return getFile(getAddress());
    }
    public static File getFile(UUID address){
        return new File(MCComputer.getComponentFolder(), address.toString()+".json");
    }
    public ItemStack getItem(){
        ItemStack item = new ItemStack(Material.IRON_PLATE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cComputer Component");
        meta.setLore(new ArrayList<String>(){{
            add(" ");
            add("§7Type§8: §e"+getComponentName());
            add("§7Address: §e"+getAddress().toString());
            add(" ");
        }});
        item.setItemMeta(meta);
        return item;
    }
    public void save(){
        try {
            FileOutputStream fos = new FileOutputStream(getFile());
            JsonObject json = new JsonObject();
            json.addProperty("type", getComponentName());
            json.add("data", toJson());
            fos.write(gson.toJson(json).getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public V8Object makeV8(V8 runtime){
        V8Object object = new V8Object(runtime);
        object.add("address", getAddress().toString());
        object.add("type", getComponentName());
        return object;
    }
    public static ComputerComponentImplementation load(UUID address){
        try {
            FileInputStream fis = new FileInputStream(getFile(address));
            StringBuilder sb = new StringBuilder();
            while (fis.available() > 0){
                byte[] data = new byte[Math.min(fis.available(), 4096)];
                fis.read(data);
                sb.append(new String(data, StandardCharsets.UTF_8));
            }
            fis.close();
            JsonObject json = gson.fromJson(sb.toString(), JsonObject.class);
            if(json.get("type").getAsString().equals("GPU")){
                return new GPUComponentImplementation(address, json.getAsJsonObject("data"));
            }
            if(json.get("type").getAsString().equals("FS")){
                return new FSComponentImplementation(address, json.getAsJsonObject("data"));
            }
        }catch (IOException ex){}
        return null;
    }

}
