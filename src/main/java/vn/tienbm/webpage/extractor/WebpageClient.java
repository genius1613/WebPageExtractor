package vn.tienbm.webpage.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tienbm on 25/04/2015.
 */
public class WebpageClient {
    public static String getWebpageSource(String input) {
        String line = "";
        try {
            URL url = new URL(input);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));

            String a;
            do {
                a = rd.readLine();
                line += a;

            } while (a != null);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
