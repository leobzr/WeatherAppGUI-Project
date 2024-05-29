import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// retreive weather data com API
public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        // extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // build API url request with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
        "latitude="+ latitude + "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FSao_Paulo";

        try {
            // call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check for response status
            if (conn.getResponseCode() != 200) {
                System.out.printf("Error: Could not connect to API");
                return null;
            }

            // store the JSON Data
            StringBuilder resultJSON = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNextLine()) {
                resultJSON.append(scanner.nextLine());
            }

            scanner.close();
            conn.disconnect();

            // parse through the data
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(String.valueOf(resultJSON));

            // retrieve hourly data
            JSONObject hourly  = (JSONObject) jsonObject.get("hourly");

            // getting the current hour's data
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weatherCode =  (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            // build the wather json data object that we are going to acess in the front end
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    //retrives geographic coordinates for given location name
    public static JSONArray getLocationData(String locationName) {
        // replacing any empty space with the +, as requested by the API
        locationName = locationName.replace(" ", "+");

        // build api url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            // call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check response status
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                // store the API results
                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNext()) {
                    resultJSON.append(scanner.nextLine());
                }

                // close scanner and disconnect http to save resources and performance
                scanner.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultsJSONObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

                // get the list of location data the api generated from the location name
                JSONArray locationData = (JSONArray) resultsJSONObj.get("results");
                return locationData;
            }


        } catch(Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            // connect to the API
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if could not establish any connection
        return null;
    }

    private  static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        // iterate through the time list and get what matches the current time
        for (int i = 0; i < timeList.size() ; i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date:
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current date and time
        String formattedDatetime = currentDateTime.format(formatter);

        return formattedDatetime;
    }

    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            // clear
            weatherCondition = "Clear";
        }else if(weathercode > 0L && weathercode <= 3L){
            // cloudy
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L)
                || (weathercode >= 80L && weathercode <= 99L)){
            // rain
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            // snow
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }

}
