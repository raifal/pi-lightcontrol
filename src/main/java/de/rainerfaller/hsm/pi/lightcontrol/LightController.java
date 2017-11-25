package de.rainerfaller.hsm.pi.lightcontrol;

import de.rainerfaller.hsm.pi.lightcontrol.pi.LightStatus;
import de.rainerfaller.hsm.pi.lightcontrol.pi.PiRequest;

import java.util.Map;

public class LightController {
    public void execute(PiRequest piRequest)
    {
        Map<String, LightStatus> l = piRequest.getLightSwitch();
        for ( String id: l.keySet())
        {
            new DeviceManager().switchLight( id, l.get(id) );
        }
    }
}
