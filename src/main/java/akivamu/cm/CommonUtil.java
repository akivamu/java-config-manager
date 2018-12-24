package akivamu.cm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class CommonUtil {
    public static String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append(LINE_SEPARATOR);
            }
            return content.toString();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
