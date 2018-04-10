package com.labuda.tempmonitor

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.labuda.tempmonitor.db.AppDatabase
import com.labuda.tempmonitor.db.entities.Device
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setSupportActionBar(toolbar)

        nav_view.setNavigationItemSelectedListener(this)

        Hawk.init(this).build()

        val currentDeviceAddress: String = Hawk.get("currentDevice", "")

        val currentDevice: Device
        if (currentDeviceAddress != "") {
            currentDevice = db.deviceDao().getDeviceByAddress(currentDeviceAddress)
            debug_list_of_devices.text = currentDevice.toString()
        } else {
            debug_list_of_devices.text = getString(R.string.no_device_added_yet)
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
