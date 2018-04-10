package com.labuda.tempmonitor.rest.albrecht

import com.labuda.tempmonitor.rest.albrecht.entities.Humidity
import com.labuda.tempmonitor.rest.albrecht.entities.Temperature
import com.labuda.tempmonitor.rest.albrecht.entities.TemperatureAndHumidity
import retrofit2.Call
import retrofit2.http.GET

interface AlbrechtService {

    @GET("api/v1/temperature")
    fun getTemperatue() : Call<Temperature>

    @GET("api/v1/humidity")
    fun getHumidity() : Call<Humidity>

    @GET("api/v1/temperature+humidity")
    fun getTemperatueAndHumidity() : Call<TemperatureAndHumidity>

}