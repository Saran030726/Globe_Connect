package com.example.data.weather

import android.util.Log
import com.example.data.model.PlaceEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class WeatherRepository(
    private val apiService: WeatherApiService = createDefaultApiService()
) {

    private val cache = ConcurrentHashMap<Int, CachedWeather>()

    data class CachedWeather(
        val weather: DestinationWeather,
        val timestamp: Long
    )

    companion object {
        private const val TAG = "WeatherRepository"
        private const val BASE_URL = "https://api.open-meteo.com/"
        private const val CACHE_EXPIRY_MILLIS = 15 * 60 * 1000L // 15 minutes

        fun createDefaultApiService(): WeatherApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(WeatherApiService::class.java)
        }
    }

    /**
     * Fetches current weather and 7-day forecast for a single place.
     * Tries live API first; if unavailable, returns cached or calibrated regional fallback.
     */
    suspend fun getWeatherForPlace(place: PlaceEntity): DestinationWeather = withContext(Dispatchers.IO) {
        val cached = cache[place.id]
        val now = System.currentTimeMillis()
        if (cached != null && (now - cached.timestamp) < CACHE_EXPIRY_MILLIS) {
            return@withContext cached.weather
        }

        val (lat, lon) = getCoordinates(place)

        try {
            val response = apiService.getForecast(latitude = lat, longitude = lon)
            val current = response.current
            val daily = response.daily

            val temp = current?.temperature?.toInt() ?: 28
            val feelsLike = current?.apparentTemperature?.toInt() ?: temp
            val humidity = current?.relativeHumidity ?: 60
            val windSpeed = current?.windSpeed ?: 10.0
            val weatherCode = current?.weatherCode ?: 1
            val conditionInfo = WeatherCodeInterpreter.interpret(weatherCode)

            val daysList = mutableListOf<DayForecast>()
            val dayFormat = SimpleDateFormat("EEE", Locale.US)
            val parseFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val displayFormat = SimpleDateFormat("dd MMM", Locale.US)
            val cal = Calendar.getInstance()

            if (daily != null && !daily.time.isNullOrEmpty()) {
                val dates = daily.time
                val codes = daily.weatherCode ?: emptyList()
                val maxTemps = daily.temperatureMax ?: emptyList()
                val minTemps = daily.temperatureMin ?: emptyList()
                val rainProbs = daily.precipitationProbability ?: emptyList()
                val uvIndexes = daily.uvIndexMax ?: emptyList()

                for (i in dates.indices) {
                    val rawDate = dates[i]
                    var dayName = "Day $i"
                    var formattedDate = rawDate
                    try {
                        val parsed = parseFormat.parse(rawDate)
                        if (parsed != null) {
                            cal.time = parsed
                            dayName = if (i == 0) "Today" else dayFormat.format(parsed)
                            formattedDate = displayFormat.format(parsed)
                        }
                    } catch (e: Exception) {
                        // ignore parse fallback
                    }

                    val code = codes.getOrNull(i) ?: weatherCode
                    val dayInfo = WeatherCodeInterpreter.interpret(code)
                    val maxT = maxTemps.getOrNull(i)?.toInt() ?: (temp + 2)
                    val minT = minTemps.getOrNull(i)?.toInt() ?: (temp - 5)
                    val rain = rainProbs.getOrNull(i) ?: 0
                    val uv = uvIndexes.getOrNull(i) ?: 5.0

                    daysList.add(
                        DayForecast(
                            date = formattedDate,
                            dayOfWeek = dayName,
                            weatherCode = code,
                            conditionTitle = dayInfo.title,
                            conditionEmoji = dayInfo.emoji,
                            tempMaxCelsius = maxT,
                            tempMinCelsius = minT,
                            rainProbabilityPercent = rain,
                            uvIndex = uv
                        )
                    )
                }
            }

            val destinationWeather = DestinationWeather(
                placeId = place.id,
                placeName = place.name,
                stateName = place.stateName,
                latitude = lat,
                longitude = lon,
                temperatureCelsius = temp,
                feelsLikeCelsius = feelsLike,
                humidityPercent = humidity,
                windSpeedKmh = windSpeed,
                weatherCode = weatherCode,
                conditionTitle = conditionInfo.title,
                conditionEmoji = conditionInfo.emoji,
                travelAdvisory = conditionInfo.advisory,
                dailyForecast = daysList,
                isLive = true
            )

            cache[place.id] = CachedWeather(destinationWeather, now)
            destinationWeather
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch live weather for ${place.name}: ${e.message}. Using calibrated fallback.")
            val fallback = WeatherCodeInterpreter.createFallbackWeather(
                placeId = place.id,
                placeName = place.name,
                stateName = place.stateName,
                latitude = lat,
                longitude = lon
            )
            cache[place.id] = CachedWeather(fallback, now)
            fallback
        }
    }

    /**
     * Concurrently fetches weather for a list of destinations in an itinerary.
     */
    suspend fun getWeatherForDestinations(places: List<PlaceEntity>): List<DestinationWeather> = coroutineScope {
        if (places.isEmpty()) return@coroutineScope emptyList()
        places.map { place ->
            async {
                getWeatherForPlace(place)
            }
        }.awaitAll()
    }

    private fun getCoordinates(place: PlaceEntity): Pair<Double, Double> {
        val lat = place.latitude
        val lon = place.longitude
        return if (lat != null && lon != null && lat != 0.0 && lon != 0.0) {
            Pair(lat, lon)
        } else {
            WeatherCodeInterpreter.getStateCoordinates(place.stateName)
        }
    }

    fun clearCache() {
        cache.clear()
    }
}
