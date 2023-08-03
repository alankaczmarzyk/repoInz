package inzynierka.backend.DBCommands;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

public class InsertSampleUsers {
    public static void main(String[] args) {
        // Base URI for the API
        String baseUri = "http://localhost:8081/api/v1/user";

        // Create an array of random user data
        String[] firstNames = {"Alice", "Bob", "Charlie", "David", "Eva"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Lee", "Garcia"};
        String[] roles = {"adm", "adv", "stu"};
        int numberOfUsersToAdd = 3;

        Random random = new Random();

        for (int i = 0; i < numberOfUsersToAdd; i++) {
            // Generate random user data
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String username = firstName.toLowerCase() + "." + lastName.toLowerCase();
            String password = generateRandomPassword();
            String email = username + "@example.com";
            String role = roles[random.nextInt(roles.length)];

            // Generate random appVersion (between 1.0 and 10.0)
            double appVersion = 1.0 + (10.0 - 1.0) * random.nextDouble();

            // Generate random lastPing (as a LocalDateTime)
            LocalDateTime randomDateTime = LocalDate.of(2020, Month.JANUARY, 8).atStartOfDay();

            // Format the lastPing as a String in the required format "v1.0" or "v2.4"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'v'G.M");
            String lastPing = randomDateTime.format(formatter);

            // Create JSON data for the user
            String json = "{\n" +
                    "    \"firstName\": \"" + firstName + "\",\n" +
                    "    \"lastName\": \"" + lastName + "\",\n" +
                    "    \"username\": \"" + username + "\",\n" +
                    "    \"password\": \"" + password + "\",\n" +
                    "    \"email\": \"" + email + "\",\n" +
                    "    \"roles\" : [\"" + role + "\"],\n" +
                    "    \"appVersion\": " + "2.1" + ",\n" +
                    "    \"lastPing\": \"" + lastPing + "\"\n" +
                    "}";

            // Set up the HTTP client
            HttpClient httpClient = HttpClient.newHttpClient();

            // Create a POST request with JSON data
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUri))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // Send the request and process the response
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                String responseBody = response.body();

                if (statusCode == 200) {
                    System.out.println("User " + (i + 1) + " inserted successfully.");
                    System.out.println("Response: " + responseBody);
                } else {
                    System.out.println("Failed to insert user " + (i + 1) + ". Status code: " + statusCode);
                    System.out.println("Response: " + responseBody);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static String generateRandomPassword() {
        // You can implement your own logic to generate a random password.
        // For simplicity, we'll use a fixed random password for all new users.
        return "Pass" + (new Random().nextInt(10000));
    }

}
