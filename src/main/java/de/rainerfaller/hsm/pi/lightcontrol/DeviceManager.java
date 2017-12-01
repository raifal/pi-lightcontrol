package de.rainerfaller.hsm.pi.lightcontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.rainerfaller.hsm.pi.lightcontrol.pi.LightStatus;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceManager {
    private static Logger logger = LoggerFactory.getLogger(DeviceManager.class.getName());

    public void getLightStatus() {

    }

    public void switchLight(String deviceId, LightStatus status) {
        // http://razberry.z-wave.me/docs/zwayDev.pdf
        String level = LightStatus.ON.equals(status) ? "255" : "0";
        String command = "devices[" + deviceId + "].instances[0].commandClasses[0x25].Set(" + level + ")";

        executeZWaveCommand(command);
    }

    public LightStatus lightStatus(String deviceId) {
        //http://192.168.1.66:8083/ZWaveAPI/Run/devices[8].instances[0].commandClasses[0x25].data.level.value
        String command = "devices[" + deviceId + "].instances[0].commandClasses[0x25].data.level.value";

        String result = executeZWaveCommand(command);
        return new Boolean(result) ? LightStatus.ON : LightStatus.OFF;
    }

    public List<String> allLightSwitchDeviceIds() {
        List<String> result = new ArrayList<>();
        String commandAllDevices = "devices";
        String resultAllDevices = executeZWaveCommand(commandAllDevices);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Device> devices = mapper.readValue(resultAllDevices, DeviceMap.class);

            for (Device device : devices.values()) {

                if ("1".equals(device.id)) {
                    // device 1 is strange, ignore it.
                    continue;
                }

                if (device.instances.containsKey("0")
                        && device.instances.get("0").commandClasses.containsKey("37")) {
                    result.add(device.id);
                }
            }
        } catch (IOException e) {
            logger.error("Error", e);
            throw new RuntimeException(e);
        }
        return result;
    }


    private String executeZWaveCommand(String command) {
        try {
            URL obj = new URL("http://localhost:8083/ZWaveAPI/Run/" + URLEncoder.encode(command, "UTF-8"));

            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            String user = System.getProperty("api.user");
            String password = System.getProperty("api.password");
            String authStr = user + ":" + password;
            // encode data on your side using BASE64
            byte[] bytesEncoded = Base64.encodeBase64(authStr.getBytes());
            String authEncoded = new String(bytesEncoded);
            connection.setRequestProperty("Authorization", "Basic " + authEncoded);
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseString = response.toString();
            return responseString;

        } catch (Exception e) {
            throw new RuntimeException("error executing zwave command", e);
        }
    }


    static class DeviceMap extends HashMap<String, Device> {
        public DeviceMap() {
            super();
        }
    }

    static class Device {
        public Map<String, Instance> instances;
        public Object data;
        public String id;

        public Device() {
        }
    }

    static class Instance {

        public Map<String, CommandClass> commandClasses;
        public Object data;
        public String id;

        public Instance() {

        }
    }

    static class CommandClass {
        public String name;
        public Object data;
        public String id;

        public CommandClass() {
        }
    }
}
