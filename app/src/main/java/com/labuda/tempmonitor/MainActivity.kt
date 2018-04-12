package com.labuda.tempmonitor

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.labuda.tempmonitor.db.AppDatabase
import com.labuda.tempmonitor.db.entities.Device
import com.labuda.tempmonitor.rest.albrecht.AlbrechtRepository
import com.labuda.tempmonitor.rest.albrecht.entities.TemperatureAndHumidity
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .build()

    private lateinit var currentDevice: Device

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        Hawk.init(this).build()

        val currentDeviceAddress: String = Hawk.get("currentDevice", "")

        // Obtain current device and set the labels in UI
        if (currentDeviceAddress != "") {
            currentDevice = db.deviceDao().getDeviceByAddress(currentDeviceAddress)
            device_name.text = currentDevice.name

            val navigationView: NavigationView = findViewById(R.id.nav_view)
            navigationView.menu.findItem(R.id.nav_current_device).title = currentDevice.name
        }

        refreshGauges()

        swipe_refresh.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                refreshGauges()
                swipe_refresh.isRefreshing = false
            }


        })

    }

    fun refreshGauges() {
        if (::currentDevice.isInitialized) {
            val tempAndHumidity: Call<TemperatureAndHumidity>

            when (currentDevice.serverType) {
                ServerType.ALBRECHT -> {
                    tempAndHumidity = AlbrechtRepository(currentDevice.address).getService().getTemperatueAndHumidity()
                }
            }

            tempAndHumidity.enqueue(object : Callback<TemperatureAndHumidity> {
                override fun onFailure(call: Call<TemperatureAndHumidity>?, t: Throwable?) {

                }

                override fun onResponse(call: Call<TemperatureAndHumidity>?, response: Response<TemperatureAndHumidity>?) {
                    val temperature: Float = response?.body()?.temperature!!
                    val ratio = temperature.toInt() / 5
                    val temperatureStartValue: Int = ratio * 5
                    val temperatureEndValue: Int = (ratio + 1) * 5

                    temperature_gauge.startValue = temperatureStartValue * 100
                    temperature_gauge.endValue = temperatureEndValue * 100
                    temperature_gauge.value = (temperature * 100).toInt()

                    current_temperature.text = "%.2f".format(temperature)
                    current_temperature_start_value.text = temperatureStartValue.toString()
                    current_temperature_end_value.text = temperatureEndValue.toString()


                    val pointsize = response?.body()?.humidity?.times(humidity_gauge.sweepAngle)?.toInt()!! / 100
                    current_humidity.text = "%.2f".format(response?.body()?.humidity!!)
                    humidity_gauge.pointSize = pointsize
                }
            })
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_add_new_device -> {
                val addNewDeviceIntent = Intent(this, AddNewDeviceActivity::class.java)
                startActivityForResult(addNewDeviceIntent, 1)
            }
            R.id.nav_settings -> {
                val settingsActivity = Intent(this, SettingsActivity::class.java)
                startActivity(settingsActivity)
            }
            R.id.nav_view_source_code -> {
                val viewSourceCodeBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.github_link)))
                startActivity(viewSourceCodeBrowser)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Request code 1 is new device addition
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // Refresh main activity
                this.recreate()
            }
        }
    }
}
