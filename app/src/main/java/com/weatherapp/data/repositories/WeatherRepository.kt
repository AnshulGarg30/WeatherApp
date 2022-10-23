package com.weatherapp.data.repositories

import com.weatherapp.data.local.WeatherDatabase
import com.weatherapp.data.model.WeatherDataResponse
import com.weatherapp.data.model.WeatherDetail
import com.weatherapp.data.model.WeatherForecast
import com.weatherapp.data.model.WeatherForecastDetail
import com.weatherapp.data.network.ApiInterface
import com.weatherapp.data.network.SafeApiRequest

class WeatherRepository(
    private val api: ApiInterface,
    private val db: WeatherDatabase
) : SafeApiRequest() {

    suspend fun findCityWeather(cityName: String): WeatherDataResponse = apiRequest {
        api.findCityWeatherData(cityName)
    }

    suspend fun findCityForecast(cityName: String): WeatherForecast = apiRequest {
        api.findCityWeatherForecastData(cityName)
    }

    suspend fun addWeather(weatherDetail: WeatherDetail) {
        db.getWeatherDao().addWeather(weatherDetail)
    }

    suspend fun addWeatherForecast(weatherDetail: WeatherForecastDetail) {
        db.getWeatherDao().addWeatherForecast(weatherDetail)
    }

    suspend fun fetchWeatherDetail(cityName: String): WeatherDetail? =
        db.getWeatherDao().fetchWeatherByCity(cityName)

    suspend fun fetchWeatherForecastDetail(cityName: String): WeatherForecastDetail? =
        db.getWeatherDao().fetchWeatherForecastByCity(cityName)

    suspend fun fetchAllWeatherDetails(): List<WeatherDetail> =
        db.getWeatherDao().fetchAllWeatherDetails()
}
