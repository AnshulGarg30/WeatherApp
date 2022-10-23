package com.weatherapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.data.model.WeatherDataResponse
import com.weatherapp.data.model.WeatherDetail
import com.weatherapp.data.model.WeatherForecast
import com.weatherapp.data.model.WeatherForecastDetail
import com.weatherapp.data.repositories.WeatherRepository
import com.weatherapp.ui.adapters.CustomAdapterSearchedCityTemperature
import com.weatherapp.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repository: WeatherRepository) :
    ViewModel(),CustomAdapterSearchedCityTemperature.WeatherItemSelection {

    val weatherAdapter = CustomAdapterSearchedCityTemperature(this@WeatherViewModel)

    private val _weatherLiveData =
        MutableLiveData<Event<State<WeatherDetail>>>()
    val weatherLiveData: LiveData<Event<State<WeatherDetail>>>
        get() = _weatherLiveData

    private val _weatherDetailListLiveData =
        MutableLiveData<Event<State<List<WeatherDetail>>>>()
    val weatherDetailListLiveData: LiveData<Event<State<List<WeatherDetail>>>>
        get() = _weatherDetailListLiveData

    private val _weatherForecastLiveData =
        MutableLiveData<Event<State<WeatherForecastDetail>>>()
    val weatherForecastLiveData: LiveData<Event<State<WeatherForecastDetail>>>
        get() = _weatherForecastLiveData

    private lateinit var weatherResponse: WeatherDataResponse
    private lateinit var weatherForecastResponse: WeatherForecast

    private fun findCityWeather(cityName: String) {
        _weatherLiveData.postValue(Event(State.loading()))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherResponse =
                    repository.findCityWeather(cityName)
                addWeatherDetailIntoDb(weatherResponse)
                withContext(Dispatchers.Main) {
                    val weatherDetail = WeatherDetail()
                    weatherDetail.icon = weatherResponse.weather.first().icon
                    weatherDetail.cityName = weatherResponse.name
                    weatherDetail.countryName = weatherResponse.sys.country
                    weatherDetail.temp = weatherResponse.main.temp
                    _weatherLiveData.postValue(
                        Event(
                            State.success(
                                weatherDetail
                            )
                        )
                    )
                }
            } catch (e: ApiException) {
                withContext(Dispatchers.Main) {
                    _weatherLiveData.postValue(Event(State.error(e.message ?: "")))
                }
            } catch (e: NoInternetException) {
                withContext(Dispatchers.Main) {
                    _weatherLiveData.postValue(Event(State.error(e.message ?: "")))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _weatherLiveData.postValue(
                        Event(
                            State.error(
                                e.message ?: ""
                            )
                        )
                    )
                }
            }
        }
    }

    private fun findCityForecast(cityName: String) {
        _weatherForecastLiveData.postValue(Event(State.loading()))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherForecastResponse = repository.findCityForecast(cityName)
                Log.e("forecastresponse","${weatherForecastResponse}")
                addWeatherForecastDetailIntoDb(weatherForecastResponse)
                withContext(Dispatchers.Main) {
                    val weatherDetail = WeatherForecastDetail()
                    weatherDetail.cityName = weatherForecastResponse.city.name
                    weatherDetail.countryName = weatherForecastResponse.city.country
                    weatherDetail.id = weatherForecastResponse.city.id
                    weatherDetail.listFiveDays = weatherForecastResponse.list
                    _weatherForecastLiveData.postValue(
                        Event(
                            State.success(
                                weatherDetail
                            )
                        )
                    )
                }
            } catch (e: ApiException) {
                withContext(Dispatchers.Main) {
                    _weatherForecastLiveData.postValue(Event(State.error(e.message ?: "")))
                }
            } catch (e: NoInternetException) {
                withContext(Dispatchers.Main) {
                    _weatherForecastLiveData.postValue(Event(State.error(e.message ?: "")))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _weatherForecastLiveData.postValue(
                        Event(
                            State.error(
                                e.message ?: ""
                            )
                        )
                    )
                }
            }
        }
    }

    private suspend fun addWeatherDetailIntoDb(weatherResponse: WeatherDataResponse) {
        val weatherDetail = WeatherDetail()
        weatherDetail.id = weatherResponse.id
        weatherDetail.icon = weatherResponse.weather.first().icon
        weatherDetail.cityName = weatherResponse.name.toLowerCase()
        weatherDetail.countryName = weatherResponse.sys.country
        weatherDetail.temp = weatherResponse.main.temp
        weatherDetail.dateTime = AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1)
        repository.addWeather(weatherDetail)
    }
    private suspend fun addWeatherForecastDetailIntoDb(weatherResponse: WeatherForecast) {
        val weatherDetail = WeatherForecastDetail()
        weatherDetail.id = weatherResponse.city.id
        weatherDetail.cityName = weatherResponse.city.name.toLowerCase()
        weatherDetail.countryName = weatherResponse.city.country
        weatherDetail.listFiveDays = weatherResponse.list
        repository.addWeatherForecast(weatherDetail)
    }

    fun fetchWeatherDetailFromDb(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherDetail = repository.fetchWeatherDetail(cityName.toLowerCase())
            withContext(Dispatchers.Main) {
                if (weatherDetail != null) {
                    // Return true of current date and time is greater then the saved date and time of weather searched
                    if (AppUtils.isTimeExpired(weatherDetail.dateTime)) {
                        findCityWeather(cityName)
                    } else {
                        _weatherLiveData.postValue(
                            Event(
                                State.success(
                                    weatherDetail
                                )
                            )
                        )
                    }

                } else {
                    findCityWeather(cityName)
                }
            }
        }
    }
    fun fetchWeatherForecastDetailFromDb(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherDetail = repository.fetchWeatherForecastDetail(cityName.toLowerCase())
            Log.e("weather detail in db","are $weatherDetail")
            withContext(Dispatchers.Main) {
                if (weatherDetail != null) {
                    // Return true of current date and time is greater then the saved date and time of weather searched
                    if (AppUtils.isTimeExpired(weatherDetail.dateTime)) {
                        findCityForecast(cityName)
                    }else {
                        _weatherForecastLiveData.postValue(
                            Event(
                                State.success(
                                    weatherDetail
                                )
                            )
                        )
                    }

                } else {
                    findCityForecast(cityName)
                }
            }
        }
    }

    fun fetchAllWeatherDetailsFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherDetailList = repository.fetchAllWeatherDetails()
            withContext(Dispatchers.Main) {
                _weatherDetailListLiveData.postValue(
                    Event(
                        State.success(weatherDetailList)
                    )
                )
            }
        }
    }

    override fun onWeatherDataSelect(item: WeatherDetail) {
        fetchWeatherDetailFromDb(item.cityName!!)
        fetchWeatherForecastDetailFromDb(item.cityName!!)
    }
}
