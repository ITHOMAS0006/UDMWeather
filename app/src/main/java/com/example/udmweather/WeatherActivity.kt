package com.example.udmweather

import com.example.udmweather.network.WeatherAPIService
import com.example.udmweather.network.WeatherResponse
import com.example.udmweather.network.ForecastWeatherResponse
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    private lateinit var buildingName: String
    private lateinit var weatherInfo: TextView
    private lateinit var tempUnitSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        buildingName = intent.getStringExtra("buildingName") ?: ""
        findViewById<TextView>(R.id.buildingName).text = buildingName

        weatherInfo = findViewById(R.id.weatherInfo)
        tempUnitSwitch = findViewById(R.id.tempUnitSwitch)

        val coordinates = getCoordinatesForBuilding()
        fetchWeatherData(coordinates.first, coordinates.second, true)

        tempUnitSwitch.setOnCheckedChangeListener { _, isChecked ->
            val coordinates = getCoordinatesForBuilding()
            fetchWeatherData(coordinates.first, coordinates.second, isChecked)
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double, isFahrenheit: Boolean) {
        val unit = if (isFahrenheit) "imperial" else "metric"
        val location = "$latitude,$longitude"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherAPIService::class.java)
        val call = service.getCurrentWeather(
            apiKey = "95ef05d930ce4596bfa182701232504",
            location = location,
            units = unit
        )

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                        displayWeatherData(weatherResponse, isFahrenheit)
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // Handle the error
            }
        })

        // Fetch forecast
        val forecastCall = service.getWeatherForecast(
            apiKey = "95ef05d930ce4596bfa182701232504",
            location = location,
            days = 7  // Number of days for the forecast
        )

        forecastCall.enqueue(object : Callback<ForecastWeatherResponse> {
            override fun onResponse(
                call: Call<ForecastWeatherResponse>,
                response: Response<ForecastWeatherResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { forecastWeatherResponse ->
                        displayForecastData(forecastWeatherResponse, isFahrenheit)
                    }
                }
            }

            override fun onFailure(call: Call<ForecastWeatherResponse>, t: Throwable) {
                // Handle the error
            }
        })
    }


    private fun displayWeatherData(weatherResponse: WeatherResponse, isFahrenheit: Boolean) {
        val temperature = if (isFahrenheit) {
            weatherResponse.current.temp_f
        } else {
            weatherResponse.current.temp_c
        }
        val feelsLikeTemperature = if (isFahrenheit) {
            weatherResponse.current.feelslike_f
        } else {
            weatherResponse.current.feelslike_c
        }
        val unitSymbol = if (isFahrenheit) "°F" else "°C"
        val windSpeedUnit = if (isFahrenheit) "mph" else "kph"

        weatherInfo.text = "Temperature: ${temperature}${unitSymbol}\n" +
                "Feels Like: ${feelsLikeTemperature}${unitSymbol}\n" +
                "Condition: ${weatherResponse.current.condition.text}\n" +
                "Humidity: ${weatherResponse.current.humidity}%\n" +
                "Wind: ${weatherResponse.current.wind_kph} ${windSpeedUnit}"

        val sdf = SimpleDateFormat("MM/dd/yyyy 'at' HH:mm:ss", Locale.getDefault())
        val currentDateAndTime: String = sdf.format(Date())

        val dataSourceInfo = findViewById<TextView>(R.id.dataSourceInfo)
        dataSourceInfo.text = "Data supplied by Weatherapi.com ($currentDateAndTime)"
    }
    private fun displayForecastData(forecastWeatherResponse: ForecastWeatherResponse, isFahrenheit: Boolean) {
        val weeklyForecastInfo = findViewById<TextView>(R.id.weeklyForecastInfo)
        val dayForecasts = forecastWeatherResponse.forecast.forecastday
        val weeklyForecastText = dayForecasts.joinToString(separator = "\n") { dayForecast ->
            val date = dayForecast.date
            val maxTemp = if (isFahrenheit) dayForecast.day.maxtemp_f else dayForecast.day.maxtemp_c
            val minTemp = if (isFahrenheit) dayForecast.day.mintemp_f else dayForecast.day.mintemp_c
            val tempUnit = if (isFahrenheit) "°F" else "°C"
            val condition = dayForecast.day.condition.text

            "$date - High: ${String.format(Locale.getDefault(), "%.1f%s", maxTemp, tempUnit)} - Low: ${String.format(Locale.getDefault(), "%.1f%s", minTemp, tempUnit)} - $condition"
        }

        weeklyForecastInfo.text = weeklyForecastText
    }


    private fun getCoordinatesForBuilding(): Pair<Double, Double> {
        return when (buildingName) {
            "Engineering Building" -> Pair(42.41321496769633, -83.13716529048308)
            "Student Union Building" -> Pair(42.413802246461316, -83.13723304588196)
            else -> Pair(0.0, 0.0)
        }
    }
}
