package com.klab.upright

import android.app.Application
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener

class MyApplication : Application() {
    var bt: BluetoothSPP? = null

    public fun initBluetooth(c: Context?) {
        bt = BluetoothSPP(c)

        if (!bt!!.isBluetoothAvailable) { //블루투스 사용 불가
            Toast.makeText(
                applicationContext
                , "Bluetooth is not available"
                , Toast.LENGTH_SHORT
            ).show()
        }

//        bt!!.setOnDataReceivedListener(object : OnDataReceivedListener {
//            //데이터 수신
//            var input: TextView = findViewById<TextView>(R.id.pressure)
//            override fun onDataReceived(data: ByteArray, message: String) {
//                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
//                input.text = message
//            }
//        })

        bt!!.setBluetoothConnectionListener(object : BluetoothConnectionListener {
            //연결됐을 때
            override fun onDeviceConnected(name: String, address: String) {
                Toast.makeText(
                    applicationContext
                    , "Connected to $name\n$address"
                    , Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDeviceDisconnected() { //연결해제
                Toast.makeText(
                    applicationContext
                    , "Connection lost", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDeviceConnectionFailed() { //연결실패
                Toast.makeText(
                    applicationContext
                    , "Unable to connect", Toast.LENGTH_SHORT
                ).show()
            }
        })

    }
}