package com.example.data.weather

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Interprets WMO Weather Interpretation Codes (WW) used by Open-Meteo and meteorological institutes.
 * Also provides travel weather advisories and fallback weather estimates for Indian states.
 */
object WeatherCodeInterpreter {

    data class ConditionInfo(
        val title: String,
        val emoji: String,
        val advisory: String
    )

    fun interpret(code: Int?): ConditionInfo {
        return when (code) {
            0 -> ConditionInfo(
                title = "Clear Sky",
                emoji = "☀️",
                advisory = "Ideal outdoor travel conditions. Use sunscreen during peak afternoon hours."
            )
            1 -> ConditionInfo(
                title = "Mainly Clear",
                emoji = "🌤️",
                advisory = "Excellent sightseeing weather with pleasant light breezes."
            )
            2 -> ConditionInfo(
                title = "Partly Cloudy",
                emoji = "⛅",
                advisory = "Comfortable temperatures, great for photography and heritage walks."
            )
            3 -> ConditionInfo(
                title = "Overcast",
                emoji = "☁️",
                advisory = "Cooler ambient light, comfortable for long excursions and trekking."
            )
            45, 48 -> ConditionInfo(
                title = "Misty / Fog",
                emoji = "🌫️",
                advisory = "Reduced visibility in early mornings. Drive with fog lights in ghat sections."
            )
            51, 53, 55 -> ConditionInfo(
                title = "Light Drizzle",
                emoji = "🌦️",
                advisory = "Mild sprinkles. Carry a light windbreaker or compact umbrella."
            )
            56, 57 -> ConditionInfo(
                title = "Freezing Drizzle",
                emoji = "🌨️",
                advisory = "Cold alpine precipitation. Warm thermal layers required."
            )
            61 -> ConditionInfo(
                title = "Light Rain",
                emoji = "🌦️",
                advisory = "Scattered showers. Waterproof phone pouches and umbrellas recommended."
            )
            63 -> ConditionInfo(
                title = "Moderate Rain",
                emoji = "🌧️",
                advisory = "Sustained rainfall. Plan indoor museum/palace visits or carry raincoats."
            )
            65 -> ConditionInfo(
                title = "Heavy Rain",
                emoji = "🌧️",
                advisory = "Intense rainfall. Check road status in hilly and coastal terrains."
            )
            71, 73, 75 -> ConditionInfo(
                title = "Snowfall",
                emoji = "❄️",
                advisory = "Sub-zero conditions. Heavy winter gear, boots, and gloves essential."
            )
            77 -> ConditionInfo(
                title = "Snow Grains",
                emoji = "🌨️",
                advisory = "Crisp mountain weather with light flurry. Wear warm woolens."
            )
            80, 81, 82 -> ConditionInfo(
                title = "Rain Showers",
                emoji = "🌦️",
                advisory = "Intermittent rain showers. Good to keep a flexible outdoor schedule."
            )
            85, 86 -> ConditionInfo(
                title = "Snow Showers",
                emoji = "🌨️",
                advisory = "High altitude flurry. Check mountain pass openings before travelling."
            )
            95 -> ConditionInfo(
                title = "Thunderstorm",
                emoji = "⛈️",
                advisory = "Lightning activity. Avoid open viewpoints or elevated water bodies."
            )
            96, 99 -> ConditionInfo(
                title = "Severe Thunderstorm",
                emoji = "⛈️",
                advisory = "Heavy storm with hail. Remain in sheltered hotel premises until it clears."
            )
            else -> ConditionInfo(
                title = "Fair Weather",
                emoji = "🌤️",
                advisory = "Pleasant travel conditions across the destination."
            )
        }
    }

    /**
     * Fallback representative GPS coordinates for Indian states if place coordinates are null.
     */
    fun getStateCoordinates(stateName: String): Pair<Double, Double> {
        return when (stateName.trim().lowercase()) {
            "rajasthan" -> Pair(26.9124, 75.7873) // Jaipur
            "kerala" -> Pair(9.9312, 76.2673) // Kochi
            "tamil nadu", "tamilnadu" -> Pair(13.0827, 80.2707) // Chennai
            "goa" -> Pair(15.2993, 74.1240) // Panaji
            "himachal pradesh" -> Pair(31.1048, 77.1734) // Shimla
            "uttarakhand" -> Pair(30.3165, 78.0322) // Dehradun
            "karnataka" -> Pair(12.9716, 77.5946) // Bengaluru
            "maharashtra" -> Pair(19.0760, 72.8777) // Mumbai
            "gujarat" -> Pair(23.0225, 72.5714) // Ahmedabad
            "jammu & kashmir", "jammu and kashmir" -> Pair(34.0837, 74.7973) // Srinagar
            "ladakh" -> Pair(34.1526, 77.5771) // Leh
            "madhya pradesh" -> Pair(23.2599, 77.4126) // Bhopal
            "west bengal" -> Pair(22.5726, 88.3639) // Kolkata
            "sikkim" -> Pair(27.3389, 88.6065) // Gangtok
            "assam" -> Pair(26.1445, 91.7362) // Guwahati
            "meghalaya" -> Pair(25.5788, 91.8933) // Shillong
            "punjab" -> Pair(31.6340, 74.8723) // Amritsar
            "delhi" -> Pair(28.6139, 77.2090) // New Delhi
            "uttar pradesh" -> Pair(27.1767, 78.0081) // Agra / Lucknow
            "odisha" -> Pair(20.2961, 85.8245) // Bhubaneswar
            "andhra pradesh" -> Pair(16.5062, 80.6480) // Vijayawada
            "telangana" -> Pair(17.3850, 78.4867) // Hyderabad
            "chhattisgarh" -> Pair(21.2514, 81.6296) // Raipur
            "bihar" -> Pair(25.5941, 85.1376) // Patna
            "jharkhand" -> Pair(23.3441, 85.3096) // Ranchi
            "arunachal pradesh" -> Pair(27.0844, 93.6053) // Itanagar
            "nagaland" -> Pair(25.6751, 94.1086) // Kohima
            "manipur" -> Pair(24.8170, 93.9368) // Imphal
            "mizoram" -> Pair(23.7271, 92.7176) // Aizawl
            "tripura" -> Pair(23.8315, 91.2868) // Agartala
            "andaman & nicobar", "andaman & nicobar islands" -> Pair(11.6234, 92.7265) // Port Blair
            else -> Pair(20.5937, 78.9629) // Center of India
        }
    }

    /**
     * Generates a realistic synthetic 7-day fallback weather forecast
     * for offline or intermittent network resilience.
     */
    fun createFallbackWeather(
        placeId: Int,
        placeName: String,
        stateName: String,
        latitude: Double,
        longitude: Double
    ): DestinationWeather {
        val (baseTemp, code) = when {
            stateName.contains("Himachal", true) || stateName.contains("Ladakh", true) || stateName.contains("Kashmir", true) -> Pair(14, 2)
            stateName.contains("Kerala", true) || stateName.contains("Goa", true) || stateName.contains("Tamil", true) -> Pair(29, 1)
            stateName.contains("Rajasthan", true) || stateName.contains("Gujarat", true) -> Pair(31, 0)
            else -> Pair(27, 1)
        }

        val info = interpret(code)
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.US)
        val dateFormat = SimpleDateFormat("dd MMM", Locale.US)

        val days = (0 until 7).map { offset ->
            cal.timeInMillis = System.currentTimeMillis() + offset * 86400000L
            val dayName = if (offset == 0) "Today" else dayFormat.format(cal.time)
            val dateStr = dateFormat.format(cal.time)
            val dayCode = if (offset % 3 == 0) code else (code + 1).coerceAtMost(3)
            val dayInfo = interpret(dayCode)

            DayForecast(
                date = dateStr,
                dayOfWeek = dayName,
                weatherCode = dayCode,
                conditionTitle = dayInfo.title,
                conditionEmoji = dayInfo.emoji,
                tempMaxCelsius = baseTemp + 2 + (offset % 2),
                tempMinCelsius = baseTemp - 5 - (offset % 2),
                rainProbabilityPercent = if (dayCode > 1) 25 else 5,
                uvIndex = 6.5
            )
        }

        return DestinationWeather(
            placeId = placeId,
            placeName = placeName,
            stateName = stateName,
            latitude = latitude,
            longitude = longitude,
            temperatureCelsius = baseTemp,
            feelsLikeCelsius = baseTemp + 1,
            humidityPercent = 58,
            windSpeedKmh = 12.0,
            weatherCode = code,
            conditionTitle = info.title,
            conditionEmoji = info.emoji,
            travelAdvisory = info.advisory,
            dailyForecast = days,
            isLive = false
        )
    }
}
