package com.klab.upright

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.klab.upright.sharedPreference.PreferenceManager
import com.klab.upright.ui.guide.GuideActivity
import com.klab.upright.ui.home.HomeFragment
import com.klab.upright.ui.tutorial.PostureActivity
import kotlinx.android.synthetic.main.drawer_main.*
import kotlinx.android.synthetic.main.drawer_main_header.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var bt:BluetoothSPP?=null
    val BLUETOOTH_REQUEST_CODE = 123
    val LOCATION_REQUEST_CODE = 200
    var deviceName=""
    var deviceAddress=""
    lateinit var pref: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_main)

        pref =  PreferenceManager(this)
        initView()
        checkPermission()
    }

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT>28){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_REQUEST_CODE)
            }
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),LOCATION_REQUEST_CODE)
            }
        }
    }


    private fun initView() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_analysis, R.id.navigation_memo))
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.gradient_background))
//        window.statusBarColor = ContextCompat.getColor(this,R.color.white)
//        window.navigationBarColor = ContextCompat.getColor(this,R.color.grey)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        (application as MyApplication).initBluetooth(this)
        bt = (application as MyApplication).bt
        initBtn()
    }

    private fun initBtn() {
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.action_textSize -> {
                    val numberPicker = NumberPicker(this)
                    numberPicker.minValue = 1
                    numberPicker.maxValue = 3
                    val builder = AlertDialog.Builder(this)
                        .setTitle("Font Size Level")
                        .setPositiveButton("OK",object: DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            pref.setFontSize(numberPicker.value)
                            recreate()
                        }
                    }).setNegativeButton("CANCEL",null)
                    builder.setView(numberPicker)
                    builder.create()
                    builder.show()
                }
                R.id.action_notify -> {
                    Toast.makeText(this, "Notice", Toast.LENGTH_SHORT).show()
                }
                R.id.action_guide -> {
                    val intent = Intent(this, GuideActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Tutorial", Toast.LENGTH_SHORT).show()
                }
                R.id.action_help -> {
                    Toast.makeText(this, "Customer Service", Toast.LENGTH_SHORT).show()
                }
                R.id.action_version -> {
                    Toast.makeText(this, "Version : 1.0.0", Toast.LENGTH_SHORT).show()
                }
                R.id.action_set_posture -> {
                    val intent = Intent(this, PostureActivity::class.java)
                    startActivity(intent)
                }
            }
            return@setNavigationItemSelectedListener false
        }

        navigationView.getHeaderView(0).bluetoothBtn.setOnClickListener {
            onClickBlueTooth()
        }
    }

    private fun onClickBlueTooth() {
        if(bt != null){
            if (bt!!.serviceState == BluetoothState.STATE_CONNECTED) {
                bt!!.disconnect()
            } else {
                val intent = Intent(this, BLEConnectActivity::class.java)
                startActivityForResult(intent, BLUETOOTH_REQUEST_CODE)
            }
        }
        Toast.makeText(this,"initbluetooth",Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == BLUETOOTH_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                deviceName = data?.getStringExtra(EXTRAS_DEVICE_NAME)!!
                deviceAddress = data.getStringExtra(EXTRAS_DEVICE_ADDRESS)!!
                Toast.makeText(this, "$deviceName,$deviceAddress", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt?.connect(data)
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt?.setupService()
                bt?.startService(BluetoothState.DEVICE_OTHER)
            } else {
                Toast.makeText(
                    this
                    , "Bluetooth was not enabled."
                    , Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.drawer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_drawer -> {
                layout_drawer_main.openDrawer(GravityCompat.END)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() { //뒤로가기 처리
        if(layout_drawer_main.isDrawerOpen(GravityCompat.END)){
            layout_drawer_main.closeDrawers()
        } else{
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        bt?.stopService()
    }

    override fun onStart() {
        super.onStart()
        if(bt != null){
            /*
            if (!bt!!.isBluetoothEnabled) { //
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT)
            } else {
                if (!bt!!.isServiceAvailable) {
                    bt!!.setupService()
                    bt!!.startService(BluetoothState.DEVICE_OTHER) //DEVICE_ANDROID는 안드로이드 기기 끼리
                }
            }
             */
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Toast.makeText(this,"location permission",Toast.LENGTH_SHORT).show()
    }

    fun getData_Name():String = deviceName
    fun getData_Adress():String = deviceAddress

    companion object {
        const val EXTRAS_DEVICE_NAME = "DEVICE_NAME"
        const val EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"
        val UUID_DATA_WRITE: UUID = UUID.fromString("0000fff2-0000-1000-8000-00805F9B34FB")
    }
}

