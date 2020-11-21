package telegram.bot.Parsing;

import com.github.prominence.openweathermap.api.HourlyForecastRequester;
import com.github.prominence.openweathermap.api.OpenWeatherMapManager;
import com.github.prominence.openweathermap.api.constants.Accuracy;
import com.github.prominence.openweathermap.api.constants.Language;
import com.github.prominence.openweathermap.api.constants.Unit;
import com.github.prominence.openweathermap.api.model.response.HourlyForecast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenWeatherParsing {
    OpenWeatherMapManager openWeatherMapManager;
    String apiToken;
    HttpURLConnection urlConnection;
    private URL url;
    private BufferedReader bufferedReader;
    JSONParsing jsonParsing;

    public OpenWeatherParsing() {
        apiToken = "2d5ba0d2e4b46c41d9037fc141689ff8";
    }

    public void setApiToken(String apiToken) { this.apiToken = apiToken; }

    public String getApiToken() { return apiToken; }

//    public String getCurWeatherByCity(String city) {
//        openWeatherMapManager = new OpenWeatherMapManager(apiToken);
//        WeatherRequester weatherRequester = openWeatherMapManager.getWeatherRequester();
//        Weather weatherResponse = weatherRequester
//                .setLanguage(Language.RUSSIAN)
//                .setUnitSystem(Unit.METRIC_SYSTEM)
//                .setAccuracy(Accuracy.ACCURATE)
//                .getByCityName(city);
//        return weatherResponse.toString();
//    }

    public String getCurWeatherByCity(String city) {
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiToken);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseMessage().equals("Not Found"))
                return "Такого города не существует.\nПовторите попытку";
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            System.out.println(bufferedReader.readLine());
            jsonParsing = new JSONParsing();
//            readJSON(urlConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTomWeatherByCity(String city) {
        openWeatherMapManager = new OpenWeatherMapManager(apiToken);
        String[] strList;
        HourlyForecastRequester forecastRequester = openWeatherMapManager.getHourlyForecastRequester();
        HourlyForecast forecastResponce = forecastRequester
                .setLanguage(Language.RUSSIAN)
                .setUnitSystem(Unit.METRIC_SYSTEM)
                .setAccuracy(Accuracy.ACCURATE)
                .getByCityName(city);
        strList = forecastResponce.toString().split("\n");
        return strList[0] + "\n" + strList[1] + "\n" + String.join("\n", strList[11].trim().split(";"));
    }

//    public String getTomWeatherByCity(String city) {
//        try {
//            url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=55.75&lon=37.62&exclude=current,minutely,daily,alerts&appid=" + apiToken);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            if (urlConnection.getResponseMessage().equals("Not Found"))
//                return "Такого города не существует.\nПовторите попытку";
//            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//            System.out.println(bufferedReader.readLine());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static void main(String[] args) throws IOException {
        OpenWeatherParsing openWeatherParsing = new OpenWeatherParsing();
        openWeatherParsing.getCurWeatherByCity("Moscow");
        JSONParsing jsonParsing = new JSONParsing();
        jsonParsing.readJSON();
        System.out.println(jsonParsing.getNamecity());
        System.out.println(jsonParsing.getLon());
        System.out.println(jsonParsing.getLat());
    }
}
