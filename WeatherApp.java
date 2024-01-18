//Piper Howell
//Interactive weather app

//This will grab live data based off the location you requested and tell you the current temperature as well as the weather condition! 

package proj2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String apiKey = "2ceacf5ea1fcda1fd3f67851a00f6b59";
        System.out.println("For best results please use this format: City, State, Country\n    Example: Radford, Virginia, US");

        while (true) {
            System.out.print("\nEnter the city (or type 'quit' to exit): ");
            String city = scanner.nextLine().trim();
            if (city.equalsIgnoreCase("quit")) {
                break;
            }
            
            fetchAndDisplayWeather(city, apiKey);
        }

        scanner.close();
    }

    private static void fetchAndDisplayWeather(String city, String apiKey) {
        try {
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

            // Create a URL object and open an HTTP connection
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response from the connection
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                // Read and parse the JSON response as plain text
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON data using regular expressions
                String jsonString = response.toString();

                String temperaturePattern = "\"temp\":(.*?),";
                String descriptionPattern = "\"description\":\"(.*?)\",";

                Pattern temperature = Pattern.compile(temperaturePattern);
                Pattern description = Pattern.compile(descriptionPattern);

                Matcher temperatureMatcher = temperature.matcher(jsonString);
                Matcher descriptionMatcher = description.matcher(jsonString);

                if (temperatureMatcher.find() && descriptionMatcher.find()) {
                    double tempKelvin = Double.parseDouble(temperatureMatcher.group(1));
                    double tempFahrenheit = (tempKelvin - 273.15) * 9/5 + 32;
                    String weatherDescription = descriptionMatcher.group(1);
                    System.out.println("Weather in " + city + ":");
                    System.out.println("Temperature: " + tempFahrenheit + "°F");
                    System.out.println("Weather Condition: " + weatherDescription);
                } else {
                    System.err.println("Failed to parse weather data for " + city);
                }
            } else {
                System.err.println("Failed to fetch weather data for " + city + ". HTTP status code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
