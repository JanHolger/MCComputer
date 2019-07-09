package eu.bebendorf.mccomputer.execution;

import com.eclipsesource.v8.*;
import eu.bebendorf.mccomputer.ComputerComponentImplementation;
import eu.bebendorf.mccomputer.api.Computer;
import eu.bebendorf.mccomputer.api.ComputerComponent;
import eu.bebendorf.mccomputer.api.execution.RuntimeEvent;
import eu.bebendorf.mccomputer.api.execution.RuntimeEventBuilder;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ComputerRuntime {

    private Computer computer;
    @Getter
    private V8 runtime = null;
    private Queue<RuntimeEventBuilderImplementation> eventQueue = new ConcurrentLinkedQueue<>();
    private long startTime = 0;
    private boolean killRequested = false;

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
            runtime.registerResource(biosAPI);
            V8BiosAPI bios = new V8BiosAPI();
            biosAPI.registerJavaMethod(bios, "pull", "pull", new Class[0]);
            biosAPI.registerJavaMethod(bios, "dispatch", "dispatch", new Class[]{String.class, V8Object.class});
            biosAPI.registerJavaMethod(bios, "sleep", "sleep", new Class[]{long.class});
            biosAPI.registerJavaMethod(bios, "time", "time", new Class[0]);
            biosAPI.registerJavaMethod(bios, "clock", "clock", new Class[0]);
            biosAPI.registerJavaMethod(bios, "getComponents", "getComponents", new Class[0]);
            biosAPI.registerJavaMethod(bios, "getComponent", "getComponent", new Class[]{String.class});
            biosAPI.registerJavaMethod(bios, "exec", "exec", new Class[]{String.class});
            biosAPI.registerJavaMethod(System.out, "println", "debug", new Class[]{String.class});
            runtime.add("BIOS", biosAPI);
            startTime = System.currentTimeMillis();
            killRequested = false;
            try {
                runtime.executeVoidScript("let __BOOTFS__ = null;let components = BIOS.getComponents();for(let i=0; i<components.length; i++){let component = components[i];if(component.type !== 'FS'){continue;}if(component.exists('boot/init.js')){__BOOTFS__ = component.address;break;}}delete components;if(__BOOTFS__ !== null){BIOS.exec(BIOS.getComponent(__BOOTFS__).read('boot/init.js'));}");
            }catch (Exception ex){
                if(!killRequested)
                    ex.printStackTrace();
            }
            runtime.release();
            runtime = null;
        }).start();
    }

    //NOTE: This doesn't instantly kill it. It takes some time to terminate (up to 20 seconds)
    public void kill(){
        if(!isRunning())
            return;
        if(killRequested)
            return;
        killRequested = true;
        runtime.terminateExecution();
    }

    public RuntimeEventBuilder event(String eventName){
        return new RuntimeEventBuilderImplementation(this, eventName);
    }

    public RuntimeEventBuilder event(RuntimeEvent event){
        return event(event.getValue());
    }

    public class V8BiosAPI {
        public V8Object pull(){
            RuntimeEventBuilderImplementation event = eventQueue.poll();
            if(event == null)
                return null;
            return event.build();
        }
        public void dispatch(String eventName, V8Object params){
            RuntimeEventBuilderImplementation event = (RuntimeEventBuilderImplementation) event(eventName);
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
        public int clock(){
            return (int)(System.currentTimeMillis() - startTime);
        }
        public int time(){
            return (int)(System.currentTimeMillis() / 1000L);
        }
        public V8Array getComponents(){
            V8Array array = new V8Array(runtime);
            runtime.registerResource(array);
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

    public void dispatch(RuntimeEventBuilderImplementation event){
        eventQueue.add(event);
    }

}
