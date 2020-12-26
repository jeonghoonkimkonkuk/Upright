package com.klab.upright

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.klab.upright.BluetoothLeService.Companion.ACTION_DATA_AVAILABLE
import com.klab.upright.BluetoothLeService.Companion.ACTION_GATT_CONNECTED
import com.klab.upright.BluetoothLeService.Companion.ACTION_GATT_DISCONNECTED
import com.klab.upright.BluetoothLeService.Companion.ACTION_GATT_SERVICES_DISCOVERED
import kotlinx.android.synthetic.main.gatt_services_characteristics.*
import java.util.*


class DeviceControlActivity : AppCompatActivity() {

    val LIST_NAME = "NAME"
    val LIST_UUID = "UUID"
    val unknown_characteristic = "Unknown characteristic"
    val unknown_service = "Unknown service"

            private var mGattCharacteristics =
        ArrayList<ArrayList<BluetoothGattCharacteristic>>()

    private lateinit var deviceName: String
    private lateinit var deviceAddress: String

    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null
    private var mWriteCharacteristic: BluetoothGattCharacteristic? = null

    // Code to manage Service lifecycle.
    private var bluetoothLeService: BluetoothLeService?=null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            bluetoothLeService?.connect(deviceAddress)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bluetoothLeService = null
        }
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read or notification operations.
    var mConnected: Boolean = false
    private val gattUpdateReceiver = object: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            Log.d("Log_BroadCast_Intent: ", intent.action)
            when(intent.action) {
                ACTION_GATT_CONNECTED -> {
                    mConnected = true
                    Toast.makeText(this@DeviceControlActivity, "BLE: Connected to device", Toast.LENGTH_SHORT).show()
                    updateConnectionState(R.string.connected)
                    invalidateOptionsMenu()

                }
                ACTION_GATT_DISCONNECTED -> {
                    mConnected = false
                    Toast.makeText(this@DeviceControlActivity, "BLE: Disconnected to device", Toast.LENGTH_SHORT).show()
                    updateConnectionState(R.string.disconnected)
                    invalidateOptionsMenu()
                    clearUI()
                }
                ACTION_GATT_SERVICES_DISCOVERED -> {
                    displayGattServices(bluetoothLeService?.getSupportedGattServices())
                }
                ACTION_DATA_AVAILABLE -> {
                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA)!!)
                    updateData()
//                    bluetoothLeService.let {
//                        val data = intent.getStringExtra(EXTRA_DATA)
//                        Log.d("Log_Data", data)
//                        if (data != null) {
//                            Log.d("Log_Data", data)
//                            displayData(data)
//                        }
//                    }
                }
            }
        }
    }


    private fun clearUI() {
        data_value.text = getText(R.string.no_data)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gatt_services_characteristics)

        //BLEConnectActivity 로 부터 intent 받기
        deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME)!!
        deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS)!!
        Log.d("Log_Device_Info", "DeviceName: $deviceName, DeviceAddress: $deviceAddress")
        device_address.text = deviceAddress

        //GATT Intent
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE)

//        bluetoothGatt.setCharacteristicNotification(characteristic, enabled)
//        val descriptor = characteristic.getDescriptor(UUID_DATA_WRITE).apply {
//            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
//        }
//        bluetoothGatt.writeDescriptor(descriptor)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        if (bluetoothLeService != null) {
            val result: Boolean = bluetoothLeService!!.connect(deviceAddress)
            Log.d("Log_connection_request", "Connect request result=$result")
        }else{
            Log.d("Log_connection_request", "Connect request result=null")
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        bluetoothLeService = null
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.gatt_services, menu)
        if (mConnected) {
            menu.findItem(R.id.menu_connect).isVisible = false
            menu.findItem(R.id.menu_disconnect).isVisible = true
        } else {
            menu.findItem(R.id.menu_connect).isVisible = true
            menu.findItem(R.id.menu_disconnect).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_connect -> {
                bluetoothLeService?.connect(deviceAddress)
                return true
            }
            R.id.menu_disconnect -> {
                bluetoothLeService?.disconnect()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateConnectionState(resourceId: Int) {
        runOnUiThread { connection_state.text = getText(resourceId) }
    }

    private fun displayData(data: String) {
        runOnUiThread { data_value.text = data }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView on the UI.
    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {

        if (gattServices == null) return

        var uuid = ""
        val gattServiceData = ArrayList<HashMap<String, String>>()
        val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String>>>()
        mGattCharacteristics = ArrayList()

        // Loops through available GATT Services.

        // Loops through available GATT Services.
        for (gattService in gattServices) {
            val currentServiceData =
                HashMap<String, String>()
            uuid = gattService.uuid.toString()
            val temp = SampleGattAttributes().lookup(uuid, unknown_service)
            currentServiceData[LIST_NAME] = temp.toString()
            currentServiceData[LIST_UUID] = uuid
            gattServiceData.add(currentServiceData)
            val gattCharacteristicGroupData =
                ArrayList<HashMap<String, String>>()
            val gattCharacteristics =
                gattService.characteristics
            val charas =
                ArrayList<BluetoothGattCharacteristic>()

            // Loops through available Characteristics.
            for (gattCharacteristic in gattCharacteristics) {
                charas.add(gattCharacteristic)
                val currentCharaData =
                    HashMap<String, String>()
                uuid = gattCharacteristic.uuid.toString()
                val temp2 = SampleGattAttributes().lookup(uuid, unknown_characteristic)
                currentCharaData[LIST_NAME] = temp2.toString()
                currentCharaData[LIST_UUID] = uuid
                gattCharacteristicGroupData.add(currentCharaData)
            }
            mGattCharacteristics.add(charas)
            gattCharacteristicData.add(gattCharacteristicGroupData)
        }

//        val gattServiceAdapter = DataListAdapter(this,listener,gattServiceData,gattCharacteristicData)
//        data_recyclerview.adapter = gattServiceAdapter
//        Log.d("inae","set adapter")
        val gattServiceAdapter = SimpleExpandableListAdapter(
            this,
            gattServiceData,
            android.R.layout.simple_expandable_list_item_2,
            arrayOf(LIST_NAME,LIST_UUID),
            intArrayOf(android.R.id.text1, android.R.id.text2),
            gattCharacteristicData,
            android.R.layout.simple_expandable_list_item_2,
            arrayOf(LIST_NAME,LIST_UUID),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        data_recyclerview.setAdapter(gattServiceAdapter)
        updateData()
        data_recyclerview.setOnChildClickListener(object:ExpandableListView.OnChildClickListener{
            override fun onChildClick(
                parent: ExpandableListView?,
                view: View?,
                groupPosition: Int,
                childPosition: Int,
                id: Long
            ): Boolean {
                Log.d("inae click position",groupPosition.toString()+","+childPosition.toString())

                if (mGattCharacteristics != null) {
                    val characteristic =
                        mGattCharacteristics[groupPosition][childPosition]
                    Log.d("inae",characteristic.toString())
                    val charaProp = characteristic.properties
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
                        if (mNotifyCharacteristic != null) {
                            bluetoothLeService?.setCharacteristicNotification(
                                mNotifyCharacteristic!!, false
                            )
                            mNotifyCharacteristic = null
                        }
                        bluetoothLeService?.readCharacteristic(characteristic)
                    }
                    if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        mNotifyCharacteristic = characteristic
                        bluetoothLeService?.setCharacteristicNotification(
                            characteristic, true
                        )
                    }
                    return true
                }
                return false
            }

        })
//        mGattServicesList.setAdapter(gattServiceAdapter)
    }

    private fun sendData(data: String) {
        mWriteCharacteristic?.let {
            if (it.properties or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0)
                bluetoothLeService?.writeCharacteristic(it, data)
        }

        mNotifyCharacteristic?.let {
            if (it.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0)
                bluetoothLeService?.setCharacteristicNotification(it, true)
        }
    }

    private fun updateData(){
        val characteristic = mGattCharacteristics[2][0]
        val charaProp = characteristic.properties
        if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
            Log.d("inae updateData read","1")
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            if (mNotifyCharacteristic != null) {
                bluetoothLeService?.setCharacteristicNotification(
                    mNotifyCharacteristic!!, false
                )
                mNotifyCharacteristic = null
            }
            bluetoothLeService?.readCharacteristic(characteristic)
        }
        if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            Log.d("inae updateData notify","2")
            mNotifyCharacteristic = characteristic
            bluetoothLeService?.setCharacteristicNotification(
                characteristic, true
            )
        }

    }




    companion object {
        const val EXTRAS_DEVICE_NAME = "DEVICE_NAME"
        const val EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"
        val UUID_DATA_WRITE: UUID = UUID.fromString("0000fff2-0000-1000-8000-00805F9B34FB")
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_GATT_CONNECTED)
        intentFilter.addAction(ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(ACTION_DATA_AVAILABLE)
        return intentFilter
    }
}
