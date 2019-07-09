package eu.bebendorf.mccomputer.execution;

import com.eclipsesource.v8.*;
import eu.bebendorf.mccomputer.ComputerComponentImplementation;
import eu.bebendorf.mccomputer.api.Computer;
import eu.bebendorf.mccomputer.api.ComputerComponent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ComputerRuntime {

    private Computer computer;
    private V8 runtime = null;
    private Queue<ComputerRuntimeEvent> eventQueue = new ConcurrentLinkedQueue<>();

    public ComputerRuntime(Computer computer){
        this.computer = computer;
    }

    public boolean isRunning(){
        return runtime != null;
    }

    public void start(){
        if(runtime != null)
            return;
        new Thread(() -> {
            runtime = V8.createV8Runtime();
            V8Object biosAPI = new V8Object(runtime);
            V8BiosAPI bios = new V8BiosAPI();
            biosAPI.registerJavaMethod(bios, "pull", "pull", new Class[0]);
            biosAPI.registerJavaMethod(bios, "dispatch", "dispatch", new Class[]{String.class, V8Object.class});
            biosAPI.registerJavaMethod(bios, "sleep", "sleep", new Class[]{long.class});
            biosAPI.registerJavaMethod(bios, "time", "time", new Class[0]);
            biosAPI.registerJavaMethod(bios, "getComponents", "getComponents", new Class[0]);
            biosAPI.registerJavaMethod(bios, "getComponent", "getComponent", new Class[]{String.class});
            biosAPI.registerJavaMethod(bios, "exec", "exec", new Class[]{String.class});
            biosAPI.registerJavaMethod(System.out, "println", "debug", new Class[]{String.class});
            runtime.add("BIOS", biosAPI);
            try {
                runtime.executeVoidScript("let __BOOTFS__ = null;let components = BIOS.getComponents();for(let i=0; i<components.length; i++){let component = components[i];if(component.type !== 'FS'){continue;}if(component.exists('boot/init.js')){__BOOTFS__ = component.address;break;}}delete components;if(__BOOTFS__ !== null){BIOS.exec(BIOS.getComponent(__BOOTFS__).read('boot/init.js'));}");
            }catch (Exception ex){
                ex.printStackTrace();
            }
            runtime = null;
        }).start();
    }

    public void kill(){
        if(!isRunning())
            return;
        runtime.terminateExecution();
        runtime = null;
    }

    public ComputerRuntimeEvent event(String eventName){
        return new ComputerRuntimeEvent(eventName);
    }

    public ComputerRuntimeEvent event(Event event){
        return event(event.getValue());
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @AllArgsConstructor
    public enum Event {
        SHUTDOWN("shutdown");
        String value;
    }

    public class V8BiosAPI {
        public V8Object pull(){
            ComputerRuntimeEvent event = eventQueue.poll();
            if(event == null)
                return null;
            return event.build();
        }
        public void dispatch(String eventName, V8Object params){
            ComputerRuntimeEvent event = event(eventName);
            for(String key : params.getKeys()){
                event.param(key, params.get(key));
            }
            event.dispatch();
        }
        public void sleep(long millis){
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {}
        }
        public long time(){
            return System.currentTimeMillis();
        }
        public V8Array getComponents(){
            V8Array array = new V8Array(runtime);
            for(ComputerComponent component : computer.getComponents()){
                ComputerComponentImplementation implementation = (ComputerComponentImplementation) component;
                array.push(implementation.makeV8(runtime));
            }
            return array;
        }
        public V8Object getComponent(String addressString){
            UUID address = UUID.fromString(addressString);
            ComputerComponent component = computer.getComponent(address);
            if(component == null)
                return null;
            ComputerComponentImplementation implementation = (ComputerComponentImplementation) component;
            return implementation.makeV8(runtime);
        }
        public void exec(String script){
            runtime.executeVoidScript(script);
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class ComputerRuntimeEvent {
        private String eventName;
        private Map<String, Object> params = new HashMap<>();
        ComputerRuntimeEvent(String eventName){
            this.eventName = eventName;
        }
        public ComputerRuntimeEvent param(String key, String value){
            params.put(key, value);
            return this;
        }
        public ComputerRuntimeEvent param(String key, int value){
            params.put(key, value);
            return this;
        }
        public ComputerRuntimeEvent param(String key, double value){
            params.put(key, value);
            return this;
        }
        public ComputerRuntimeEvent param(String key, long value){
            params.put(key, value);
            return this;
        }
        public ComputerRuntimeEvent param(String key, short value){
            params.put(key, value);
            return this;
        }
        public ComputerRuntimeEvent param(String key, float value){
            params.put(key, value);
            return this;
        }
        ComputerRuntimeEvent param(String key, Object value){
            params.put(key, value);
            return this;
        }
        public void dispatch(){
            eventQueue.add(this);
        }
        V8Object build(){
            V8Object object = new V8Object(runtime);
            object.add("event", eventName);
            for(String key : params.keySet()) {
                Object o = params.get(key);
                if(o.getClass().equals(String.class)){
                    object.add(key, (String) o);
                }
                if(o.getClass().equals(int.class)){
                    object.add(key, (int) o);
                }
                if(o.getClass().equals(double.class)){
                    object.add(key, (double) o);
                }
                if(o.getClass().equals(float.class)){
                    object.add(key, (float) o);
                }
                if(o.getClass().equals(long.class)){
                    object.add(key, (long) o);
                }
                if(o.getClass().equals(short.class)){
                    object.add(key, (short) o);
                }
                if(o.getClass().equals(V8Object.class)){
                    object.add(key, (V8Object) o);
                }
                if(o.getClass().equals(V8Array.class)){
                    object.add(key, (V8Array) o);
                }
            }
            return object;
        }
    }

}
