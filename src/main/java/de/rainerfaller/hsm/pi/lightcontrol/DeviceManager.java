package de.rainerfaller.hsm.pi.lightcontrol;

import de.rainerfaller.hsm.pi.lightcontrol.pi.LightStatus;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class DeviceManager {
    private static Logger logger = LoggerFactory.getLogger(DeviceManager.class.getName());

    public void getLightStatus() {

    }

    public void switchLight(String deviceId, LightStatus status) {
        String level = LightStatus.ON.equals(status) ? "255" : "0";
        String command = "devices[" + deviceId + "].instances[0].commandClasses[0x25].Set(" + level + ")";

        executeZWaveCommand(command);
    }



    private void executeZWaveCommand(String command) {
        try {

            logger.info("command " + command);

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
            logger.info("res " + responseString);

        } catch (Exception e) {
            logger.error("error executing zwave command", e);
        }
    }


}
