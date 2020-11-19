package telegram.bot;

import com.github.prominence.openweathermap.api.HourlyForecastRequester;
import com.github.prominence.openweathermap.api.OpenWeatherMapManager;
import com.github.prominence.openweathermap.api.WeatherRequester;
import com.github.prominence.openweathermap.api.constants.Accuracy;
import com.github.prominence.openweathermap.api.constants.Language;
import com.github.prominence.openweathermap.api.constants.Unit;
import com.github.prominence.openweathermap.api.model.response.HourlyForecast;
import com.github.prominence.openweathermap.api.model.response.Weather;

public class OpenWeatherParsing {
    OpenWeatherMapManager openWeatherMapManager;

    public OpenWeatherParsing() {
         openWeatherMapManager = new OpenWeatherMapManager("2d5ba0d2e4b46c41d9037fc141689ff8");
    }

    public String getCurWeatherByCity(String city) {
        WeatherRequester weatherRequester = openWeatherMapManager.getWeatherRequester();
        Weather weatherResponse = weatherRequester
                .setLanguage(Language.RUSSIAN)
                .setUnitSystem(Unit.METRIC_SYSTEM)
                .setAccuracy(Accuracy.ACCURATE)
                .getByCityName(city);
        return weatherResponse.toString();
    }

    public String getTomWeatherByCity(String city) {
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
}
