import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TestAI {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:64854/ai/api/spark/enterpriseFilterByAI");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            
            File file = new File("test_enterprise_filter.json");
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    requestBody.append(line);
                }
            }
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Content: " + response.toString());
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}