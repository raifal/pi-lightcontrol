package de.rainerfaller.hsm.pi.lightcontrol;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {
        logger.warn("MyMessage");
        SpringApplication.run(new Object[]{Application.class}, args);
    }
}