package telegram.bot;

import com.github.prominence.openweathermap.api.HourlyForecastRequester;
import com.github.prominence.openweathermap.api.OpenWeatherMapManager;
import com.github.prominence.openweathermap.api.WeatherRequester;
import com.github.prominence.openweathermap.api.constants.Accuracy;
import com.github.prominence.openweathermap.api.constants.Language;
import com.github.prominence.openweathermap.api.constants.Unit;

public class OpenWeatherParsing {
    OpenWeatherMapManager openWeatherMapManager;

    public OpenWeatherParsing() {
         openWeatherMapManager = new OpenWeatherMapManager("2d5ba0d2e4b46c41d9037fc141689ff8");
    }

    public String getCurWeatherByCity(String city) {
        WeatherRequester weatherRequester = openWeatherMapManager.getWeatherRequester();
        return weatherRequester
                .setLanguage(Language.RUSSIAN)
                .setUnitSystem(Unit.METRIC_SYSTEM)
                .setAccuracy(Accuracy.ACCURATE)
                .getByCityName(city)
                .toString();
    }

    public String getTomWeatherByCity(String city) {
        HourlyForecastRequester forecastRequester = openWeatherMapManager.getHourlyForecastRequester();
        return forecastRequester
                .setLanguage(Language.RUSSIAN)
                .setUnitSystem(Unit.METRIC_SYSTEM)
                .setAccuracy(Accuracy.ACCURATE)
                .getByCityName(city)
                .toString();
    }
}
