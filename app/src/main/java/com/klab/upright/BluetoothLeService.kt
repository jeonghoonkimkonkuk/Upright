package com.klab.upright

import android.app.Service
import android.bluetooth.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*


class BluetoothLeService: Service() {

    val STATE_DISCONNECTED = 0
    val STATE_CONNECTING = 1
    val STATE_CONNECTED = 2

    var connectionState = STATE_DISCONNECTED
    private var bluetoothGatt: BluetoothGatt? = null
    private var deviceAddress: String = ""
    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    inner class LocalBinder: Binder() {
        val service = this@BluetoothLeService
    }

    private val binder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder? = binder
    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    fun connect(address: String): Boolean {
        bluetoothGatt?.let {
            if (address == deviceAddress) {
                return if (it.connect()) {
                    connectionState = STATE_CONNECTING
                    true
                } else false
            }
        }

        val device = bluetoothAdapter.getRemoteDevice(address)
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        deviceAddress = address
        connectionState = STATE_CONNECTING
        return true
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    private fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    // Various callback methods defined by the BLE API.
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            val intentAction: String
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    intentAction = ACTION_GATT_CONNECTED
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(intentAction)
//                    broadcastUpdate(ACTION_DATA_AVAILABLE)
                    Log.d("Log_GATT_SERVER", "Connected to GATT server.")
                    Log.d("Log_GATT_SERVER", "Attempting to start service discovery:" + bluetoothGatt?.discoverServices())
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
                    connectionState = STATE_DISCONNECTED
                    Log.d("Log_GATT_SERVER", "Disconnected from GATT server.")
                    broadcastUpdate(intentAction)
                }

            }
        }

        // New services discovered
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                else -> Log.d("Log_GATT_SERVER", "onServicesDiscovered received: $status")
            }
        }

        // Result of a characteristic read operation
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.d("Log_GATT_SERVER: ", characteristic.toString())
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.d("Log_GATT_SERVER: ", "onCharacteristicRead")
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            if (characteristic != null) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        Log.d("Log_broadcastUpdate", intent.toString() + action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)
        //characteristic.getStringValue(0)
        var temp = characteristic.value

        val zero:Byte = 0
        val arr:String = ""
        for(i in temp.indices){
            if(temp[i]==zero){
                temp = temp.copyOfRange(0,i)
                break;
            }

        }

        val data= String(temp)
//        Log.d("Log_broadcastUpdate_temp", temp)

        if (data?.isNotEmpty() == true) {

//            val hexString: String = data.joinToString(separator = ",")
            intent.putExtra(EXTRA_DATA, "$data")
            sendBroadcast(intent)
            Log.d("Log_broadcastUpdate_value", data.toString())
        }

//            intent.putExtra(EXTRA_DATA, characteristic.getStringValue(0))

    }

    fun getSupportedGattServices(): MutableList<BluetoothGattService>? {
        return bluetoothGatt?.services
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic?) {
        if (bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bluetoothGatt!!.readCharacteristic(characteristic)
    }


    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enabled: Boolean
    ) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w("inae", "BluetoothAdapter not initialized")
            return
        }
        bluetoothGatt?.setCharacteristicNotification(characteristic, enabled)

        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG).apply {
            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        }
        bluetoothGatt?.writeDescriptor(descriptor)
    }

    fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        data: String
    ) {
        characteristic.setValue(data)
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        bluetoothGatt?.writeCharacteristic(characteristic)
    }


    companion object {
        const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"

        val UUID_DATA_NOTIFY: UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
        val UUID_DATA_WRITE: UUID = UUID.fromString("0000fff2-0000-1000-8000-00805F9B34FB")
        var CLIENT_CHARACTERISTIC_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
