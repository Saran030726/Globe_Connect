package com.example.data.weather

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenMeteoResponse(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "elevation") val elevation: Double? = null,
    @Json(name = "timezone") val timezone: String? = null,
    @Json(name = "current") val current: CurrentWeatherDto? = null,
    @Json(name = "daily") val daily: DailyWeatherDto? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @Json(name = "time") val time: String? = null,
    @Json(name = "temperature_2m") val temperature: Double? = null,
    @Json(name = "relative_humidity_2m") val relativeHumidity: Int? = null,
    @Json(name = "apparent_temperature") val apparentTemperature: Double? = null,
    @Json(name = "weather_code") val weatherCode: Int? = null,
    @Json(name = "wind_speed_10m") val windSpeed: Double? = null
)

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    @Json(name = "time") val time: List<String>? = null,
    @Json(name = "weather_code") val weatherCode: List<Int>? = null,
    @Json(name = "temperature_2m_max") val temperatureMax: List<Double>? = null,
    @Json(name = "temperature_2m_min") val temperatureMin: List<Double>? = null,
    @Json(name = "precipitation_probability_max") val precipitationProbability: List<Int>? = null,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>? = null
)

/**
 * Domain model representing a single day's forecast.
 */
data class DayForecast(
    val date: String,
    val dayOfWeek: String,
    val weatherCode: Int,
    val conditionTitle: String,
    val conditionEmoji: String,
    val tempMaxCelsius: Int,
    val tempMinCelsius: Int,
    val rainProbabilityPercent: Int,
    val uvIndex: Double
)

/**
 * Domain model representing real-time and 7-day forecast for a destination in an itinerary.
 */
data class DestinationWeather(
    val placeId: Int,
    val placeName: String,
    val stateName: String,
    val latitude: Double,
    val longitude: Double,
    val temperatureCelsius: Int,
    val feelsLikeCelsius: Int,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val weatherCode: Int,
    val conditionTitle: String,
    val conditionEmoji: String,
    val travelAdvisory: String,
    val dailyForecast: List<DayForecast> = emptyList(),
    val isLive: Boolean = true,
    val fetchedAtMillis: Long = System.currentTimeMillis()
)

sealed interface WeatherUiState {
    object Idle : WeatherUiState
    object Loading : WeatherUiState
    data class Success(val forecasts: List<DestinationWeather>) : WeatherUiState
    data class Error(val message: String, val fallbackForecasts: List<DestinationWeather> = emptyList()) : WeatherUiState
}
