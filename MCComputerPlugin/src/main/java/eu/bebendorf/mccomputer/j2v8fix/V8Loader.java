package eu.bebendorf.mccomputer.j2v8fix;

import com.eclipsesource.v8.V8;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class V8Loader {

    public static boolean load(){
        try {
            V8.getActiveRuntimes();
            return true;
        }catch (NoClassDefFoundError e){}
        File libFile = new File("plugins/MCComputer/j2v8.jar");
        if(!libFile.exists())
            return false;
        try {
            addSoftwareLibrary(libFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void addSoftwareLibrary(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
    }

}
