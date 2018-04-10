package com.labuda.tempmonitor

import android.app.Activity
import android.arch.persistence.room.Room
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import com.labuda.tempmonitor.db.AppDatabase
import com.labuda.tempmonitor.db.entities.Device
import com.labuda.tempmonitor.rest.albrecht.AlbrechtRepository
import com.labuda.tempmonitor.rest.albrecht.AlbrechtService
import com.labuda.tempmonitor.rest.albrecht.entities.Temperature
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_add_new_device.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class AddNewDeviceActivity : AppCompatActivity() {

    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.new_device_title)
        setContentView(R.layout.activity_add_new_device)

        val arrayAdapter: ArrayAdapter<CharSequence> = ArrayAdapter(this, android.R.layout.simple_spinner_item, ServerType.values().map { serverType -> serverType.name })
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        add_new_device_server_types.adapter = arrayAdapter

        add_new_device_address.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (getServerType() == ServerType.ALBRECHT) {
                    try {
                        val deviceRepo = AlbrechtRepository(p0.toString()).getService().getTemperatue()

                        deviceRepo.enqueue(object : Callback<Temperature> {
                            override fun onFailure(call: Call<Temperature>?, t: Throwable?) {

                            }

                            override fun onResponse(call: Call<Temperature>?, response: Response<Temperature>?) {
                                add_new_device_name.setText(response?.body()?.name)
                            }
                        })
                    } catch (e: IllegalArgumentException) {
                        print(e)
                    }
                }
            }
        })

        add_new_device_add_button.setOnClickListener({ _ ->

            val newDevice = Device(add_new_device_name.text.toString(),
                    add_new_device_address.text.toString(),
                    getServerType(),
                    Date())

            db.deviceDao().insert(newDevice)

            Hawk.init(this).build()

            if (Hawk.get("currentDevice", "") == "") {
                Hawk.put("currentDevice", newDevice.address)
                setResult(Activity.RESULT_OK)
            }

            setResult(-1)
            this.finish()
        })
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        this.finish()
    }

    private fun getServerType(): ServerType {
        return ServerType.valueOf(add_new_device_server_types.selectedItem.toString())
    }
}
