package eu.bebendorf.mccomputer.api.execution;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public enum RuntimeEvent {
    SHUTDOWN("bios.shutdown"),
    CLICK("bios.gpu.click");
    String value;
}