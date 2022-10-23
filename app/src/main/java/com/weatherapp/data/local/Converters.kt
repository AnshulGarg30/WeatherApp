package com.weatherapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.weatherapp.data.model.WeatherDataResponse


class Converters {

    @TypeConverter
    fun listToJson(value: List<WeatherDataResponse>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<WeatherDataResponse>::class.java).toList()
}