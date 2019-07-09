package eu.bebendorf.mccomputer.api.execution;

public interface RuntimeEventBuilder {
    RuntimeEventBuilder param(String key, String value);
    RuntimeEventBuilder param(String key, int value);
    RuntimeEventBuilder param(String key, double value);
    RuntimeEventBuilder param(String key, short value);
    RuntimeEventBuilder param(String key, float value);
    void dispatch();
}
