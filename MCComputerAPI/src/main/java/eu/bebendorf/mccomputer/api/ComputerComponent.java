package eu.bebendorf.mccomputer.api;

import java.util.UUID;

public interface ComputerComponent {

    UUID getAddress();
    String getComponentName();

}
