package telegram.bot.Parsing;

import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeatherParsing {
    String apiToken;
    HttpURLConnection urlConnection;
    private URL url;
    JSONParsingCurrentWeather jsonParsing;
    JSONParsingTomorrowWeather jsonParsingTomorrowWeather;

    public OpenWeatherParsing() {
        apiToken = "2d5ba0d2e4b46c41d9037fc141689ff8";
    }

    public void setApiToken(String apiToken) { this.apiToken = apiToken; }

    public String getApiToken() { return apiToken; }

    public String getCurWeatherByCity(String city) throws Exception {
        url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiToken);
        urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection.getResponseMessage().equals("Not Found"))
            return "Такого города не существует.\nПовторите попытку";
        jsonParsing = new JSONParsingCurrentWeather();
        jsonParsing.readJSON(urlConnection);
        return jsonParsing.toString();
    }

    public String getTomWeatherByCity(String city) throws Exception {
        Double lat, lon;
        url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiToken);
        urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection.getResponseMessage().equals("Not Found"))
            return "Такого города не существует.\nПовторите попытку";
        jsonParsing = new JSONParsingCurrentWeather();
        jsonParsing.readJSON(urlConnection);
        lat = jsonParsing.getLat();
        lon = jsonParsing.getLon();
        url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon +
                "&exclude=current,minutely,daily,alerts&appid=" + apiToken);
        urlConnection = (HttpURLConnection) url.openConnection();
        jsonParsingTomorrowWeather = new JSONParsingTomorrowWeather();
        jsonParsingTomorrowWeather.setCity(jsonParsing.getCity());
        jsonParsingTomorrowWeather.setLat(lat);
        jsonParsingTomorrowWeather.setLon(lon);
        jsonParsingTomorrowWeather.setId(jsonParsing.getId());
        jsonParsingTomorrowWeather.setCountry(jsonParsing.getCountry());
        jsonParsingTomorrowWeather.readJSON(urlConnection);
        return jsonParsingTomorrowWeather.toString();
    }
}
