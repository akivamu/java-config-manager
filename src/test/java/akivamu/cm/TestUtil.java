package akivamu.cm;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtil {
    private static final JsonParser jsonParser = new JsonParser();

    public static String readFileFromResources(String filePath) {
        InputStream is = TestUtil.class.getResourceAsStream(filePath);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder fileContents = new StringBuilder();
        try {
            String line;
            while ((line = br.readLine()) != null) {
                fileContents.append(line);
                fileContents.append(System.getProperty("line.separator"));
            }
        } catch (IOException ignored) {
        } finally {
            try {
                br.close();
                isr.close();
                is.close();
            } catch (IOException ignored) {
            }
        }
        return fileContents.toString();
    }

    public static JsonObject readJsonObjectFromResources(String filePath) {
        return jsonParser.parse(readFileFromResources(filePath)).getAsJsonObject();
    }
}
