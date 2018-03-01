package com.labuda.tempmonitor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class AddNewDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.new_device_title)
        setContentView(R.layout.activity_add_new_device)
    }
}
