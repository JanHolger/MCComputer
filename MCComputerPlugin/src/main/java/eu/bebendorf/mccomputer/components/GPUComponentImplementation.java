package eu.bebendorf.mccomputer.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import eu.bebendorf.mccomputer.ComputerComponentImplementation;
import eu.bebendorf.mccomputer.api.components.GPUComponent;
import eu.bebendorf.mcscreen.api.Screen;
import eu.bebendorf.mcscreen.api.ScreenAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GPUComponentImplementation extends ComputerComponentImplementation implements GPUComponent {

    private List<Integer> screens = new ArrayList<>();

    public GPUComponentImplementation() {
        super(null);
    }

    public GPUComponentImplementation(UUID address, JsonObject json) {
        super(address);
        screens = new ArrayList<>();
        for(JsonElement je : json.get("screens").getAsJsonArray()){
            screens.add(je.getAsInt());
        }
        save();
    }

    public List<Screen> getScreens() {
        List<Screen> screenList = new ArrayList<>();
        for(int id : screens){
            Screen screen = ScreenAPI.getInstance().getScreen(id);
            if(screen != null)
                screenList.add(screen);
            else
                screens.remove(id);
        }
        return screenList;
    }

    public void addScreen(Screen screen) {
        if(!screens.contains(screen.getId())){
            screens.add(screen.getId());
            save();
        }
    }

    public void removeScreen(Screen screen) {
        if(screens.contains(screen.getId())) {
            screens.remove(screen.getId());
            save();
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray screens = new JsonArray();
        for(Screen screen : getScreens()){
            screens.add(new JsonPrimitive(screen.getId()));
        }
        json.add("screens", screens);
        return json;
    }

}
