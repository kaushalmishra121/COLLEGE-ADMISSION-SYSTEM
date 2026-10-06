import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Static files (HTML, CSS, JS)
        server.createContext("/", new StaticHandler());

        // API: Submit application & Fetch applications
        server.createContext("/api/applications", new ApiHandler());

        server.setExecutor(null);
        System.out.println("=================================================");
        System.out.println(" College Admission System Server Running");
        System.out.println(" URL: http://localhost:" + PORT);
        System.out.println("=================================================");
        server.start();
    }

    // Serves index.html, style.css, script.js
    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            File file = new File("." + path);
            if (!file.exists() || file.isDirectory()) {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            String contentType = "text/plain";
            if (path.endsWith(".html")) contentType = "text/html; charset=UTF-8";
            else if (path.endsWith(".css")) contentType = "text/css; charset=UTF-8";
            else if (path.endsWith(".js")) contentType = "application/javascript; charset=UTF-8";

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());

            OutputStream os = exchange.getResponseBody();
            Files.copy(file.toPath(), os);
            os.close();
        }
    }

    // Handles database requests via JDBC
    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Return all applications from MySQL as JSON
                StringBuilder json = new StringBuilder("[");
                String sql = "SELECT application_id, student_name, email, phone, hsc_percentage, program, status FROM applications ORDER BY application_id DESC";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/admissions_db", "root", AdmissionDAO.getPass());
                     Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery(sql)) {

                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append(String.format("{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"score\":%.2f,\"program\":\"%s\",\"status\":\"%s\"}",
                                rs.getInt("application_id"),
                                rs.getString("student_name"),
                                rs.getString("email"),
                                rs.getString("phone") != null ? rs.getString("phone") : "",
                                rs.getDouble("hsc_percentage"),
                                rs.getString("program"),
                                rs.getString("status")));
                        first = false;
                    }
                } catch (Exception e) {
                    System.err.println("[Server] Database fetch error: " + e.getMessage());
                }
                json.append("]");

                byte[] resBytes = json.toString().getBytes("UTF-8");
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, resBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(resBytes);
                os.close();
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Read request body from form
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }

                // Simple JSON parser
                String data = body.toString();
                String name = extract(data, "name");
                String email = extract(data, "email");
                String phone = extract(data, "phone");
                String scoreStr = extract(data, "score");
                double score = 0.0;
                try {
                    if (!scoreStr.isEmpty()) score = Double.parseDouble(scoreStr);
                } catch (NumberFormatException ignored) {}
                String course = extract(data, "program");

                // 1. Save to MySQL database via JDBC
                boolean saved = AdmissionDAO.saveApplication(name, email, phone, score, course);

                // 2. Dispatch background confirmation email to applicant
                EmailService.sendAdmissionEmail(email, name, course);

                String resp = "{\"message\":\"Saved\",\"status\":\"success\",\"emailDispatched\":true}";
                byte[] respBytes = resp.getBytes("UTF-8");
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, respBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(respBytes);
                os.close();
            }
        }

        private String extract(String json, String key) {
            String target = "\"" + key + "\":\"";
            int start = json.indexOf(target);
            if (start != -1) {
                start += target.length();
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
            // For numbers without quotes
            String numTarget = "\"" + key + "\":";
            start = json.indexOf(numTarget);
            if (start != -1) {
                start += numTarget.length();
                int end = json.indexOf(",", start);
                if (end == -1) end = json.indexOf("}", start);
                return json.substring(start, end).trim();
            }
            return "";
        }
    }
}