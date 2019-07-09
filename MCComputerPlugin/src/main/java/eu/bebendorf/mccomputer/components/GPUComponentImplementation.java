package eu.bebendorf.mccomputer.components;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import eu.bebendorf.mccomputer.ComputerComponentImplementation;
import eu.bebendorf.mccomputer.api.components.GPUComponent;
import eu.bebendorf.mcscreen.api.Screen;
import eu.bebendorf.mcscreen.api.ScreenAPI;
import eu.bebendorf.mcscreen.api.helper.ImageWrapper;
import lombok.AllArgsConstructor;

import java.util.*;

public class GPUComponentImplementation extends ComputerComponentImplementation implements GPUComponent {

    private List<Integer> screens = new ArrayList<>();
    private Map<Integer, ImageWrapper> images = new HashMap<>();

    public GPUComponentImplementation() {
        super(null);
    }

    public GPUComponentImplementation(UUID address, JsonObject json) {
        super(address);
        screens = new ArrayList<>();
        for(JsonElement je : json.get("screens").getAsJsonArray()){
            screens.add(je.getAsInt());
        }
        for(Screen screen : getScreens()){
            images.put(screen.getId(), new ImageWrapper(screen.getPixelWidth(), screen.getPixelHeight()));
        }
        save();
    }

    public List<Screen> getScreens() {
        List<Screen> screenList = new ArrayList<>();
        for(int id : new ArrayList<>(screens)){
            Screen screen = ScreenAPI.getInstance().getScreen(id);
            if(screen != null)
                screenList.add(screen);
            else
                screens.remove(id);
        }
        return screenList;
    }

    public Screen getScreen(int id){
        for(Screen screen : getScreens()){
            if(screen.getId() == id)
                return screen;
        }
        return null;
    }

    public void addScreen(Screen screen) {
        if(!screens.contains(screen.getId())){
            screens.add(screen.getId());
            images.put(screen.getId(), new ImageWrapper(screen.getPixelWidth(), screen.getPixelHeight()));
            save();
        }
    }

    public void removeScreen(Screen screen) {
        if(screens.contains(screen.getId())) {
            screens.remove(screen.getId());
            images.remove(screen.getId());
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

    public V8Object makeV8(V8 runtime){
        V8Object object = super.makeV8(runtime);
        V8GPUAPI gpuApi = new V8GPUAPI(runtime);
        object.registerJavaMethod(gpuApi, "getScreens", "getScreens", new Class[0]);
        object.registerJavaMethod(gpuApi, "getScreen", "getScreen", new Class[]{int.class});
        return object;
    }

    @AllArgsConstructor
    public class V8GPUAPI {
        private V8 runtime;
        public V8Array getScreens(){
            V8Array array = new V8Array(runtime);
            runtime.registerResource(array);
            for(Screen screen : GPUComponentImplementation.this.getScreens()){
                array.push(makeScreen(screen));
            }
            return array;
        }
        public V8Object getScreen(int id){
            Screen screen = GPUComponentImplementation.this.getScreen(id);
            if(screen == null)
                return null;
            return makeScreen(screen);
        }
        private V8Object makeScreen(Screen screen){
            V8Object object = new V8Object(runtime);
            runtime.registerResource(object);
            V8ScreenAPI screenAPI = new V8ScreenAPI(screen);
            object.add("id", screen.getId());
            object.add("width", screen.getPixelWidth());
            object.add("height", screen.getPixelHeight());
            object.registerJavaMethod(screenAPI, "refresh", "refresh", new Class[]{});
            object.registerJavaMethod(screenAPI, "setPixel", "setPixel", new Class[]{int.class,int.class,int.class,int.class,int.class});
            object.registerJavaMethod(screenAPI, "fillCircle", "fillCircle", new Class[]{int.class,int.class,int.class,int.class,int.class,int.class});
            object.registerJavaMethod(screenAPI, "fillRect", "fillRect", new Class[]{int.class,int.class,int.class,int.class,int.class,int.class,int.class});
            return object;
        }
        @AllArgsConstructor
        public class V8ScreenAPI {
            private Screen screen;
            public void setPixel(int x, int y, int r, int g, int b){
                ImageWrapper.WrappedPixel pixel = getImage().getPixel(x, y);
                if(pixel != null){
                    pixel.setRGBA(r, g, b, 255);
                }
            }
            public void fillRect(int x, int y, int width, int height, int r, int g, int b){
                ImageWrapper image = images.get(screen.getId());
                for(int xW = 0; xW < width; xW++){
                    for(int yW = 0; yW < height; yW++){
                        ImageWrapper.WrappedPixel pixel = image.getPixel(x+xW, y+yW);
                        if(pixel != null){
                            pixel.setRGBA(r, g, b, 255);
                        }
                    }
                }
            }
            public void fillCircle(int x, int y, int radius, int r, int g, int b){
                ImageWrapper image = images.get(screen.getId());
                for(int xW = x-radius; xW <= x+radius; xW++){
                    for(int yW = y-radius; yW < y+radius; yW++){
                        double d = Math.sqrt(Math.pow(Math.max(x, xW)-Math.min(x, xW),2)+Math.pow(Math.max(y, yW)-Math.min(y, yW),2));
                        if(d <= radius){
                            ImageWrapper.WrappedPixel pixel = image.getPixel(xW, yW);
                            if(pixel != null){
                                pixel.setRGBA(r, g, b, 255);
                            }
                        }
                    }
                }
            }
            public void refresh(){
                screen.render(getImage());
            }
            private ImageWrapper getImage(){
                ImageWrapper image = images.get(screen.getId());
                if(image == null){
                    image = new ImageWrapper(screen.getPixelWidth(), screen.getPixelHeight());
                }
                return image;
            }
        }

    }

}
