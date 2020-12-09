package telegram.bot.Parsing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import telegram.bot.Parsing.JSONProcessing.JSONParsingCurrentWeather;
import telegram.bot.Parsing.JSONProcessing.JSONParsingTomorrowWeather;
import telegram.bot.Parsing.JSONProcessing.JSONParsingWeekWeather;

import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeatherParsing {
    private final Logger log = LogManager.getLogger(OpenWeatherParsing.class);
    String apiToken;
    HttpURLConnection urlConnection;
    private URL url;
    JSONParsingCurrentWeather jsonParsingCurrentWeather;

    public OpenWeatherParsing() { apiToken = "2d5ba0d2e4b46c41d9037fc141689ff8"; }

    public void setApiToken(String apiToken) { this.apiToken = apiToken; }

    public String getApiToken() { return apiToken; }

    public void getURLConnectionByCurWeather(String city) {
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiToken);
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (Exception e) { log.error("Connection to the weather service failed", e); }
    }

    public String getCurWeatherByCity(String city) {
        try {
            getURLConnectionByCurWeather(city);
            if (urlConnection.getResponseMessage().equals("Not Found"))
                return "Такого города не существует.\nПовторите попытку";
            jsonParsingCurrentWeather = new JSONParsingCurrentWeather();
            jsonParsingCurrentWeather.readJSON(urlConnection);
            urlConnection.disconnect();
            return jsonParsingCurrentWeather.toString();
        } catch (Exception e) { log.error("Connection to the weather service failed", e); }
        return null;
    }

    public String getTomWeatherByCity(String city){
        try {
            Double lat, lon;
            getURLConnectionByCurWeather(city);
            if (urlConnection.getResponseMessage().equals("Not Found"))
                return "Такого города не существует.\nПовторите попытку";
            jsonParsingCurrentWeather = new JSONParsingCurrentWeather();
            jsonParsingCurrentWeather.readJSON(urlConnection);
            urlConnection.disconnect();
            lat = jsonParsingCurrentWeather.getLat();
            lon = jsonParsingCurrentWeather.getLon();
            url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon +
                    "&exclude=current,minutely,daily,alerts&appid=" + apiToken);
            urlConnection = (HttpURLConnection) url.openConnection();
            JSONParsingTomorrowWeather jsonParsingTomorrowWeather = new JSONParsingTomorrowWeather();
            jsonParsingTomorrowWeather.setCity(jsonParsingCurrentWeather.getCity());
            jsonParsingTomorrowWeather.setLat(lat);
            jsonParsingTomorrowWeather.setLon(lon);
            jsonParsingTomorrowWeather.setId(jsonParsingCurrentWeather.getId());
            jsonParsingTomorrowWeather.setCountry(jsonParsingCurrentWeather.getCountry());
            jsonParsingTomorrowWeather.readJSON(urlConnection);
            urlConnection.disconnect();
            return jsonParsingTomorrowWeather.toString();
        } catch (Exception e) { log.error("Connection to the weather service failed", e); }
        return null;
    }

    public String getWeekWeatherByCity(String city) {
        try {
            Double lat, lon;
            getURLConnectionByCurWeather(city);
            if (urlConnection.getResponseMessage().equals("Not Found"))
                return "Такого города не существует.\nПовторите попытку";
            jsonParsingCurrentWeather = new JSONParsingCurrentWeather();
            jsonParsingCurrentWeather.readJSON(urlConnection);
            urlConnection.disconnect();
            lat = jsonParsingCurrentWeather.getLat();
            lon = jsonParsingCurrentWeather.getLon();
            url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon +
                    "&exclude=current,minutely,hourly,alerts&appid=" + apiToken);
            urlConnection = (HttpURLConnection) url.openConnection();
            JSONParsingWeekWeather jsonParsingWeekWeather = new JSONParsingWeekWeather();
            jsonParsingWeekWeather.setCity(jsonParsingCurrentWeather.getCity());
            jsonParsingWeekWeather.setLat(lat);
            jsonParsingWeekWeather.setLon(lon);
            jsonParsingWeekWeather.setId(jsonParsingCurrentWeather.getId());
            jsonParsingWeekWeather.setCountry(jsonParsingCurrentWeather.getCountry());
            jsonParsingWeekWeather.readJSON(urlConnection);
            urlConnection.disconnect();
            return jsonParsingWeekWeather.toString();
        } catch (Exception e) { log.error("Connection to the weather service failed", e); }
        return null;
    }
}
