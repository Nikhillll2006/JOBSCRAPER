import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;
import java.net.InetSocketAddress;

public class SimpleLoginServer {

    private static Map<String, String> profileData = new HashMap<>();
    private static final String PROFILE_FILE = "profileData.txt";

    public static void main(String[] args) throws IOException {
        // Load profile data from the file at the server start
        loadProfileData();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/login", new LoginHandler());
        server.createContext("/profile", new ProfileHandler());
        server.createContext("/dashboard", new DashboardHandler());
        server.createContext("/jobsearch", new JobSearchHandler());  // Added jobsearch handler
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8080");
    }

    // Load profile data from the file
    private static void loadProfileData() {
        File file = new File(PROFILE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] keyValue = line.split("=");
                    if (keyValue.length == 2) {
                        profileData.put(keyValue[0], keyValue[1]);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading profile data: " + e.getMessage());
            }
        }
    }

    // Save profile data to the file
    private static void saveProfileData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROFILE_FILE))) {
            for (Map.Entry<String, String> entry : profileData.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving profile data: " + e.getMessage());
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String[] credentials = reader.readLine().split("&");
                String username = credentials[0].split("=")[1];
                String password = credentials[1].split("=")[1];

                System.out.println("Received credentials: " + username + ", " + password);

                if ("Nikhil".equals(username) && "1234".equals(password)) {
                    // Login successful, redirect to profile page
                    String response = "{\"message\": \"Login successful!\", \"redirect\": \"/profile\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    // Invalid login
                    String response = "{\"message\": \"Invalid username or password.\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(401, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        }
    }

    static class ProfileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                // Parse the form data (assuming it's sent as key=value pairs)
                String[] fields = requestBody.toString().split("&");
                for (String field : fields) {
                    String[] keyValue = field.split("=");
                    if (keyValue.length == 2) {
                        profileData.put(keyValue[0], keyValue[1]);
                    }
                }

                // Store the profile data temporarily in memory
                System.out.println("Profile data stored: " + profileData);

                // Save profile data to the file
                saveProfileData();

                // Respond with a success message
                String response = "{\"message\": \"Profile saved successfully!\", \"redirect\": \"/jobsearch\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Display the profile data
            String dashboard = "<html><body><h1>Welcome to the Dashboard</h1><p>Profile Data:</p><pre>" + profileData + "</pre></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, dashboard.length());
            OutputStream os = exchange.getResponseBody();
            os.write(dashboard.getBytes());
            os.close();
        }
    }

    // New handler for serving the jobsearch.html file
    static class JobSearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Serve the jobsearch.html file
            File file = new File("jobsearch.html");
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] fileContent = new byte[(int) file.length()];
                    fis.read(fileContent);

                    // Send the content of the file as a response
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, fileContent.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(fileContent);
                    os.close();
                }
            } else {
                // If the file is not found, send a 404 response
                String response = "<html><body><h1>404 Not Found</h1><p>jobsearch.html not found.</p></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
