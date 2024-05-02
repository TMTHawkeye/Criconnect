package com.example.criconnect.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.criconnect.R
import com.example.criconnect.databinding.FragmentOrganizerCreationBinding
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar

class OrganizerCreationFragment : Fragment() {
    lateinit var binding: FragmentOrganizerCreationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrganizerCreationBinding.inflate(layoutInflater)

        binding.button.setOnClickListener {
            findWeather()
        }
        return binding.root
    }

    fun findWeather() {
        val city: String = binding.editTextTextPersonName.getText().toString()
        val url =
            "http://api.openweathermap.org/data/2.5/weather?q=$city&appid=462f445106adc1d21494341838c10019&units=metric"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            object : Response.Listener<String?> {
                override fun onResponse(response: String?) {
                    try {
                        //find temperature
                        val jsonObject = JSONObject(response)
                        val `object` = jsonObject.getJSONObject("main")
                        val temp = `object`.getDouble("temp")
                        binding.textView3.setText("Temperature\n$temp°C")

                        //find country
                        val object8 = jsonObject.getJSONObject("sys")
                        val count = object8.getString("country")
                        binding.country.setText("$count  :")

                        //find city
                        val city = jsonObject.getString("name")
                        binding.cityNam.setText(city)

                        //find icon
                        val jsonArray = jsonObject.getJSONArray("weather")
                        val obj = jsonArray.getJSONObject(0)
                        val icon = obj.getString("icon")
                        Picasso.get().load("http://openweathermap.org/img/wn/$icon@2x.png")
                            .into(binding.imageView)

                        //find date & time
                        val calendar = Calendar.getInstance()
                        val std = SimpleDateFormat("HH:mm a \nE, MMM dd yyyy")
                        val date = std.format(calendar.time)
                        binding.textView2.setText(date)

                        //find latitude
                        val object2 = jsonObject.getJSONObject("coord")
                        val lat_find = object2.getDouble("lat")
                        binding.latitude.setText("$lat_find°  N")

                        //find longitude
                        val object3 = jsonObject.getJSONObject("coord")
                        val long_find = object3.getDouble("lon")
                        binding.longitude.setText("$long_find°  E")

                        //find humidity
                        val object4 = jsonObject.getJSONObject("main")
                        val humidity_find = object4.getInt("humidity")
                        binding.humidity.setText("$humidity_find  %")

                        //find sunrise
                        val object5 = jsonObject.getJSONObject("sys")
                        val sunrise_find = object5.getString("sunrise")
                        binding.sunrise.setText("$sunrise_find  am")

                        //find sunrise
                        val object6 = jsonObject.getJSONObject("sys")
                        val sunset_find = object6.getString("sunset")
                        binding.sunset.setText("$sunset_find  pm")

                        //find pressure
                        val object7 = jsonObject.getJSONObject("main")
                        val pressure_find = object7.getString("pressure")
                        binding.pressure.setText("$pressure_find  hPa")

                        //find wind speed
                        val object9 = jsonObject.getJSONObject("wind")
                        val wind_find = object9.getString("speed")
                        binding.wind.setText("$wind_find  km/h")

                        //find min temperature
                        val object10 = jsonObject.getJSONObject("main")
                        val mintemp = object10.getDouble("temp_min")
                        binding.minTemp.setText("Min Temp\n$mintemp °C")

                        //find max temperature
                        val object12 = jsonObject.getJSONObject("main")
                        val maxtemp = object12.getDouble("temp_max")
                        binding.tempMax.setText("Max Temp\n$maxtemp °C")

                        //find feels
                        val object13 = jsonObject.getJSONObject("main")
                        val feels_find = object13.getDouble("feels_like")
                        binding.feels.setText("$feels_find °C")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.d("TAGWeather", "onResponse: ${e.message}")
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Log.d("TAGWeather", "onResponse: ${error.message}")

                    Toast.makeText(
                        requireContext(),
                        error.getLocalizedMessage(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }


}