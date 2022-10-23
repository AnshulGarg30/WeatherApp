package com.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weatherapp.data.model.WeatherDetail
import com.weatherapp.data.model.WeatherForecastDetail

@Dao
interface WeatherDetailDao {

    /**
     * Duplicate values are replaced in the table.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeather(weatherDetail: WeatherDetail)

    @Query("SELECT * FROM ${WeatherDetail.TABLE_NAME} WHERE cityName = :cityName")
    suspend fun fetchWeatherByCity(cityName: String): WeatherDetail?

    @Query("SELECT * FROM ${WeatherDetail.TABLE_NAME}")
    suspend fun fetchAllWeatherDetails(): List<WeatherDetail>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeatherForecast(weatherDetail: WeatherForecastDetail)

    @Query("SELECT * FROM ${WeatherForecastDetail.TABLE_FORECAST_NAME} WHERE cityName = :cityName")
    suspend fun fetchWeatherForecastByCity(cityName: String): WeatherForecastDetail?

}
