package com.yuhtin.lauren.service;

import com.yuhtin.lauren.core.logger.Logger;
import lombok.Data;
import lombok.Getter;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
public class LocaleManager {

    @Getter
    private static final LocaleManager instance = new LocaleManager();

    private String city;
    private String regionName;
    private String countryCode;

    public void searchHost(String accessKey) {

        Socket socket = new Socket();
        try {

            socket.connect(new InetSocketAddress("google.com", 80));

            String ip = socket.getInetAddress().toString().replace("google.com/", "");

            GetConnectionFactory connection = new GetConnectionFactory("http://api.ipstack.com/" + ip + "?access_key=" + accessKey);
            String response = connection.buildConnection();

            JSONObject jsonObject = new JSONObject(response);

            city = jsonObject.getString("city");
            regionName = jsonObject.getString("region_name");
            countryCode = jsonObject.getString("country_code");

        } catch (Exception exception) {

            Logger.log("Error trying to search host");
            exception.printStackTrace();

        }

    }

    public String buildMessage() {
        return city + ", " + regionName + ", " + countryCode;
    }

}
