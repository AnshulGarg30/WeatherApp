package com.weatherapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weatherapp.data.model.WeatherDetail.Companion.TABLE_NAME
import com.weatherapp.data.model.WeatherForecastDetail.Companion.TABLE_FORECAST_NAME

/**
 * Data class for Database entity and Serialization.
 */
@Entity(tableName = TABLE_NAME)
data class WeatherDetail(

    @PrimaryKey
    var id: Int? = 0,
    var temp: Double? = null,
    var icon: String? = null,
    var cityName: String? = null,
    var countryName: String? = null,
    var dateTime: String? = null,
) {
    companion object {
        const val TABLE_NAME = "weather_detail"
    }
}
@Entity(tableName = TABLE_FORECAST_NAME)
data class WeatherForecastDetail(

    @PrimaryKey
    var id: Int? = 0,
    var cityName: String? = null,
    var countryName: String? = null,
    var dateTime: String? = null,
    var listFiveDays: List<WeatherDataResponse>? = null
) {
    companion object {
        const val TABLE_FORECAST_NAME = "weather_forecast_detail"
    }
}