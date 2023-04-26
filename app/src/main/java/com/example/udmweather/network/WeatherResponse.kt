package com.example.udmweather.network

data class WeatherResponse(
    val current: CurrentWeather,
   /* val forecast: Forecast*/
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val date_epoch: Long,
    val day: Day
)

data class Day(
    val maxtemp_c: Double,
    val maxtemp_f: Double,
    val mintemp_c: Double,
    val mintemp_f: Double,
    val avgtemp_c: Double,
    val avgtemp_f: Double,
    val maxwind_mph: Double,
    val maxwind_kph: Double,
    val totalprecip_mm: Double,
    val totalprecip_in: Double,
    val avgvis_km: Double,
    val avgvis_miles: Double,
    val avghumidity: Double,
    val condition: WeatherCondition,
    val daily_will_it_rain: Int,
    val daily_will_it_snow: Int,
    val daily_chance_of_rain: String,
    val daily_chance_of_snow: String,
    val uv: Double
)
data class CurrentWeather(
    val temp_c: Double,
    val temp_f: Double,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val humidity: Int,
    val wind_kph: Double,
    val wind_mph: Double,
    val condition: WeatherCondition
)

data class WeatherCondition(
    val text: String
)
