package com.labuda.tempmonitor.rest.albrecht

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlbrechtRepository(val baseUrl: String) {

    fun getService() : AlbrechtService {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(AlbrechtService::class.java)
    }

}