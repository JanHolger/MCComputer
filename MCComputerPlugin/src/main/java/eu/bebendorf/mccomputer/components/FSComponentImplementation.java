package eu.bebendorf.mccomputer.components;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.google.gson.JsonObject;
import eu.bebendorf.mccomputer.ComputerComponentImplementation;
import eu.bebendorf.mccomputer.MCComputer;
import eu.bebendorf.mccomputer.api.components.FSComponent;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.UUID;

public class FSComponentImplementation extends ComputerComponentImplementation implements FSComponent {

    private File getFSFolder(){
        return new File(MCComputer.getHostFSFolder(), getAddress().toString());
    }

    public File getFile(String file){
        return new File(getFSFolder(), file.startsWith("/") ? file.substring(1) : file);
    }

    public FSComponentImplementation() {
        super(null);
        File folder = getFSFolder();
        folder.mkdir();
    }

    public FSComponentImplementation(UUID address, JsonObject json){
        super(address);
        save();
    }

    public byte[] read(String file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File f = getFile(file);
        if(!f.exists() || !f.isFile())
            return new byte[0];
        try {
            FileInputStream fis = new FileInputStream(f);
            while (fis.available() > 0){
                byte[] data = new byte[Math.min(fis.available(), 4096)];
                fis.read(data);
                baos.write(data);
            }
            fis.close();
        } catch (IOException e) {}
        return baos.toByteArray();
    }

    public void write(String file, byte[] data) {
        File f = getFile(file);
        if(f.exists() && !f.isFile())
            return;
        if(!f.getAbsoluteFile().getParentFile().exists())
            return;
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {}
    }

    public void delete(String file) {
        //DELETE THE FILE RECURSIVLY
    }

    public boolean exists(String file) {
        return getFile(file).exists();
    }

    public void mkdir(String file){
        getFile(file).mkdir();
    }

    public boolean isDirectory(String file) {
        return getFile(file).exists() && getFile(file).isDirectory();
    }

    public JsonObject toJson() {
        return new JsonObject();
    }

    public void remove(){
        super.remove();
        //DELETE THE FS
    }

    public V8Object makeV8(V8 runtime){
        V8Object object = super.makeV8(runtime);
        V8FSAPI api = new V8FSAPI(runtime);
        object.registerJavaMethod(api, "read", "read", new Class[]{String.class});
        object.registerJavaMethod(api, "exists", "exists", new Class[]{String.class});
        object.registerJavaMethod(api, "isDirectory", "isDirectory", new Class[]{String.class});
        object.registerJavaMethod(api, "delete", "delete", new Class[]{String.class});
        object.registerJavaMethod(api, "mkdir", "mkdir", new Class[]{String.class});
        object.registerJavaMethod(api, "write", "write", new Class[]{String.class,String.class});
        return object;
    }

    @AllArgsConstructor
    public class V8FSAPI{
        private V8 runtime;
        public String read(String fileName){
            return FSComponentImplementation.this.readString(fileName);
        }
        public void write(String fileName, String data){
            FSComponentImplementation.this.writeString(fileName, data);
        }
        public boolean exists(String fileName){
            return FSComponentImplementation.this.exists(fileName);
        }
        public void mkdir(String fileName){
            FSComponentImplementation.this.mkdir(fileName);
        }
        public void delete(String fileName){
            FSComponentImplementation.this.delete(fileName);
        }
        public boolean isDirectory(String fileName){
            return FSComponentImplementation.this.isDirectory(fileName);
        }
    }

}
