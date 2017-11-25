package de.rainerfaller.hsm.pi.lightcontrol.pi;

import java.util.HashMap;
import java.util.Map;

public class PiResponse {
    private Map<String, LightStatus> lightStatus = new HashMap<>();
    private Map<String, DoorStatus> doorStatus = new HashMap<>();

    public PiResponse() {
    }

    public Map<String, LightStatus> getLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(Map<String, LightStatus> lightStatus) {
        this.lightStatus = lightStatus;
    }

    public Map<String, DoorStatus> getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(Map<String, DoorStatus> doorStatus) {
        this.doorStatus = doorStatus;
    }
}
