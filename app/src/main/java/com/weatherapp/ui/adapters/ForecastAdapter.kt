package com.weatherapp.ui.adapters

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.weatherapp.R
import com.weatherapp.data.model.WeatherForecastDetail
import com.weatherapp.databinding.ItemForecastBinding
import com.weatherapp.util.AppConstants
import com.weatherapp.util.AppUtils
import com.weatherapp.util.AppUtils.stringToDate

class ForecastAdapter(val data: WeatherForecastDetail) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

//    private val weatherDetailList = WeatherForecastDetail()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding:ItemForecastBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_forecast,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(data,position)
    }

    override fun getItemCount(): Int = data.listFiveDays?.size!!

//    fun setData(
//        newWeatherDetail: WeatherForecastDetail
//    ) {
//        Log.e("forecast data","data is $newWeatherDetail")
//        weatherDetailList.listFiveDays = newWeatherDetail.listFiveDays
//        weatherDetailList.id = newWeatherDetail.id
//        weatherDetailList.dateTime = newWeatherDetail.dateTime
//        weatherDetailList.cityName = newWeatherDetail.cityName
//        weatherDetailList.countryName = newWeatherDetail.countryName
//        notifyDataSetChanged()
//    }

    inner class ViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(weatherDetail: WeatherForecastDetail, position: Int) {
            Log.e("forecast Adapter","$weatherDetail")
            binding.apply {
                val iconCode = weatherDetail.listFiveDays?.get(position)?.weather?.first()?.icon?.replace("n", "d")
                AppUtils.setGlideImage(
                    imageViewForecastIcon,
                    AppConstants.WEATHER_API_IMAGE_ENDPOINT + "${iconCode}@4x.png"
                )
                if(weatherDetail.listFiveDays?.get(position)?.dt_txt != null) {
                    textViewTimeOfDay.text = weatherDetail.listFiveDays?.get(position)?.dt_txt
                    Log.e(
                        "date",
                        " is ${stringToDate(weatherDetail.listFiveDays?.get(position)?.dt_txt!!)}"
                    )
                    val dayOfTheWeek = DateFormat.format(
                        "EEEE",
                        stringToDate(weatherDetail.listFiveDays?.get(position)?.dt_txt!!)
                    ) // Thursday
                    textViewDayOfWeek.text = dayOfTheWeek
                }
                textViewTemp.text = weatherDetail?.listFiveDays?.get(position)?.main?.temp.toString()
                itemmintemp.text = weatherDetail?.listFiveDays?.get(position)?.main?.tempMin.toString()
                itemmaxtemp.text = weatherDetail?.listFiveDays?.get(position)?.main?.tempMax.toString()
            }
        }
    }

}
