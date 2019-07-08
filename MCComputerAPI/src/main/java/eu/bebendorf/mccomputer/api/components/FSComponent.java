package eu.bebendorf.mccomputer.api.components;

import eu.bebendorf.mccomputer.api.ComputerComponent;

import java.nio.charset.StandardCharsets;

public interface FSComponent extends ComputerComponent {

    byte[] read(String file);
    void write(String file, byte[] data);
    void delete(String file);
    boolean exists(String file);
    void mkdir(String file);
    boolean isDirectory(String file);
    default String readString(String file){
        return new String(read(file), StandardCharsets.UTF_8);
    }
    default void writeString(String file, String data){
        write(file, data.getBytes(StandardCharsets.UTF_8));
    }

    default String getComponentName(){
        return "FS";
    }

}
