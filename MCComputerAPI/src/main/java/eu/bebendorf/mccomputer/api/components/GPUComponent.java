package eu.bebendorf.mccomputer.api.components;

import eu.bebendorf.mccomputer.api.ComputerComponent;
import eu.bebendorf.mcscreen.api.Screen;

import java.util.List;

public interface GPUComponent extends ComputerComponent {

    List<Screen> getScreens();
    void addScreen(Screen screen);
    void removeScreen(Screen screen);
    default String getComponentName(){
        return "GPU";
    }

}
