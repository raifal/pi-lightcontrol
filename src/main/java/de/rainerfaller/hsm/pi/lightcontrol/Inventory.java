package de.rainerfaller.hsm.pi.lightcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Inventory {
    private static Logger logger = LoggerFactory.getLogger(Inventory.class.getName());

    public void update()
    {
        logger.info("Updating inventory");
    }
}
