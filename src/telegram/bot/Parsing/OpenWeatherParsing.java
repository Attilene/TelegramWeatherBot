package telegram.bot.Parsing;

import com.github.prominence.openweathermap.api.HourlyForecastRequester;
import com.github.prominence.openweathermap.api.OpenWeatherMapManager;
import com.github.prominence.openweathermap.api.WeatherRequester;
import com.github.prominence.openweathermap.api.constants.Accuracy;
import com.github.prominence.openweathermap.api.constants.Language;
import com.github.prominence.openweathermap.api.constants.Unit;
import com.github.prominence.openweathermap.api.model.response.HourlyForecast;
import com.github.prominence.openweathermap.api.model.response.Weather;

import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeatherParsing {
    OpenWeatherMapManager openWeatherMapManager;
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

//    public String getTomWeatherByCity(String city) {
//        openWeatherMapManager = new OpenWeatherMapManager(apiToken);
//        String[] strList;
//        HourlyForecastRequester forecastRequester = openWeatherMapManager.getHourlyForecastRequester();
//        HourlyForecast forecastResponce = forecastRequester
//                .setLanguage(Language.RUSSIAN)
//                .setUnitSystem(Unit.METRIC_SYSTEM)
//                .setAccuracy(Accuracy.ACCURATE)
//                .getByCityName(city);
//        strList = forecastResponce.toString().split("\n");
//        return strList[0] + "\n" + strList[1] + "\n" + String.join("\n", strList[11].trim().split(";"));
//    }

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
        jsonParsingTomorrowWeather.readJSON(urlConnection);
        return jsonParsingTomorrowWeather.toString();
    }

    public static void main(String[] args) throws Exception {
        OpenWeatherParsing openWeatherParsing = new OpenWeatherParsing();
//        System.out.println(openWeatherParsing.getCurWeatherByCity("Москва"));
        System.out.println(openWeatherParsing.getTomWeatherByCity("Москва"));
    }
}
