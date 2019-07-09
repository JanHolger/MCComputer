package eu.bebendorf.mccomputer.execution;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import eu.bebendorf.mccomputer.api.execution.RuntimeEventBuilder;

import java.util.HashMap;
import java.util.Map;

public class RuntimeEventBuilderImplementation implements RuntimeEventBuilder {

    private String eventName;
    private ComputerRuntime runtime;
    private Map<String, Object> params = new HashMap<>();
    RuntimeEventBuilderImplementation(ComputerRuntime runtime, String eventName){
        this.runtime = runtime;
        this.eventName = eventName;
    }
    public RuntimeEventBuilder param(String key, String value){
        params.put(key, value);
        return this;
    }
    public RuntimeEventBuilder param(String key, int value){
        params.put(key, value);
        return this;
    }
    public RuntimeEventBuilder param(String key, double value){
        params.put(key, value);
        return this;
    }
    public RuntimeEventBuilder param(String key, long value){
        params.put(key, value);
        return this;
    }
    public RuntimeEventBuilder param(String key, short value){
        params.put(key, value);
        return this;
    }
    public RuntimeEventBuilder param(String key, float value){
        params.put(key, value);
        return this;
    }
    RuntimeEventBuilder param(String key, Object value){
        params.put(key, value);
        return this;
    }
    public void dispatch(){
        runtime.dispatch(this);
    }
    V8Object build(){
        V8Object object = new V8Object(runtime.getRuntime());
        runtime.getRuntime().registerResource(object);
        object.add("event", eventName);
        for(String key : params.keySet()) {
            Object o = params.get(key);
            if(o.getClass().equals(String.class)){
                object.add(key, (String) o);
            }
            if(o.getClass().equals(Integer.class)){
                object.add(key, (int) o);
            }
            if(o.getClass().equals(Double.class)){
                object.add(key, (double) o);
            }
            if(o.getClass().equals(Float.class)){
                object.add(key, (float) o);
            }
            if(o.getClass().equals(Short.class)){
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
