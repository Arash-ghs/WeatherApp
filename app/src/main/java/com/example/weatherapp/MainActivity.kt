package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var currentCity = "Tehran"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        refresh(binding.root)
        binding.buttonTehran.setOnClickListener{
            currentCity = "Tehran"
            refresh(binding.root)
        }
        binding.buttonKaraj.setOnClickListener{
            currentCity = "Karaj"
            refresh(binding.root)
        }
        binding.buttonMashhad.setOnClickListener{
            currentCity = "Mashhad"
            refresh(binding.root)
        }
    }

    @SuppressLint("SetTextI18n")
    fun showContent(cityName:String, description:String, imageUrl:String, sunrise:Int, sunset:Int, temp:Double, feelsLike:Double, minTemp:Double, maxTemp:Double, pressure:Int, humidity:Int, windSpeed:Int){
        binding.imageViewTower.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE

        binding.textViewCityName.text = cityName
        binding.textViewWeatherDescription.text = description
        Glide.with(this@MainActivity).load(imageUrl).into(binding.imageViewWeather)
        binding.textViewSunrise.text = getTimeFromUnixTime(sunrise)
        binding.textViewSunset.text = getTimeFromUnixTime(sunset)
        binding.textViewTemp.text = "دما: $temp"
        binding.textViewFeelsLike.text = "دما احساس شده: $feelsLike"
        binding.textViewMinTemp.text = "حداقل دما: $minTemp"
        binding.textViewMaxTemp.text = "حداکثر دما: $maxTemp"
        binding.textViewPressure.text = "فشار هوا: $pressure"
        binding.textViewHumidity.text = "رطوبت هوا: $humidity"
        binding.textViewWindSpeed.text = "سرعت باد: $windSpeed"
    }

    private fun getTimeFromUnixTime(unixTIme:Int):String{
        val time = unixTIme * 1000.toLong()
        val date = Date(time)
        val formatter = SimpleDateFormat("HH:mm a")
        return formatter.format(date)
    }

    private fun getData(city:String){
        val client = OkHttpClient()
        val request = Request.Builder().url("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=51d59b3186d80caaca7ce0a6e79fc2c1&lang=fa&units=metric").build()

        client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response) {
                getAndShowData(response.body!!.string())
            }
        })
    }

    private fun getAndShowData(rawData:String){
        val jsonObject = JSONObject(rawData)
        val weatherArray = jsonObject.getJSONArray("weather")
        val weatherObject = weatherArray.getJSONObject(0)
        val iconId = weatherObject.getString("icon")
        val imageUrl = "https://openweathermap.org/img/wn/${iconId}@2x.png"

        val sunrise = jsonObject.getJSONObject("sys").getInt("sunrise")
        val sunset = jsonObject.getJSONObject("sys").getInt("sunset")

        val temp = jsonObject.getJSONObject("main").getDouble("temp")
        val feelsLike = jsonObject.getJSONObject("main").getDouble("feels_like")
        val minTemp = jsonObject.getJSONObject("main").getDouble("temp_min")
        val maxTemp = jsonObject.getJSONObject("main").getDouble("temp_max")
        val pressure = jsonObject.getJSONObject("main").getInt("pressure")
        val humidity = jsonObject.getJSONObject("main").getInt("humidity")
        val windSpeed = jsonObject.getJSONObject("wind").getInt("speed")

        runOnUiThread{
            showContent(jsonObject.getString("name"), weatherObject.getString("description"), imageUrl, sunrise, sunset, temp, feelsLike, minTemp, maxTemp, pressure, humidity, windSpeed)
        }
    }

    fun refresh(view: View){
        binding.imageViewTower.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE

        binding.textViewCityName.text = "---"
        binding.textViewWeatherDescription.text = "---"
        binding.textViewSunrise.text = "---"
        binding.textViewSunset.text ="---"
        binding.textViewTemp.text = "---"
        binding.textViewFeelsLike.text = "---"
        binding.textViewMinTemp.text = "---"
        binding.textViewMaxTemp.text = "---"
        binding.textViewPressure.text = "---"
        binding.textViewHumidity.text = "---"
        binding.textViewWindSpeed.text = "---"

        getData(currentCity)

    }
}