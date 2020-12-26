package com.klab.upright

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_ble_connect.*


class BLEConnectActivity: AppCompatActivity() {

//    private var bluetoothGatt: BluetoothGatt? = null

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

    private lateinit var mLeDeviceAdapter: LeDeviceAdapter
    private lateinit var menu_stop:MenuItem
    private lateinit var menu_scan:MenuItem
    private lateinit var menu_refresh:MenuItem


    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) { // 기기 자체의 블루투스 어댑터
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val BluetoothAdapter.isDisabled: Boolean // 블루투스 활성화
        get() = !isEnabled

    private  val REQUEST_ENABLE_BT = 1000
    private  var mScanning: Boolean = false
    private var arrayDevices = ArrayList<BluetoothDevice>()
    private val handler = Handler()

    private val scanCallback = object : ScanCallback()
    {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d("enter Log_Scan_ScanResult", result.toString())
            result?.let {
                runOnUiThread {
                    Log.d("Log_Scan_ScanResult",arrayDevices.toString())
                    mLeDeviceAdapter.addDevice(it.device)
                    mLeDeviceAdapter.notifyDataSetChanged()
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.let {
                for (result in it) {
                    if (!arrayDevices.contains(result.device)) {
                        arrayDevices.add(result.device)
                        Log.d("Log_Scan_Batch", arrayDevices.toString())
                        mLeDeviceAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Toast.makeText(
                this@BLEConnectActivity, "BLE Scan Failed $errorCode", Toast.LENGTH_SHORT
            ).show()
        }
    }
    private val SCAN_PERIOD = 10000L
    val LOCATION_PERMISSION_REQUEST_CODE=123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble_connect)
        setTitle("Connect Device")

        //권한 검
        val permission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permission == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOfNulls<String>(1)
            permissions[0] = Manifest.permission.ACCESS_COARSE_LOCATION // 사용자에게 요청할 권한
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE) // 사용자에게 권한 요청
        }

        //블루투스 비활성화 상태라면 '설정 -> 블루투스 통신'화면으로 이
        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView_Scan.layoutManager = layoutManager
        val listener = object: LeDeviceAdapter.itemClickListener {
            override fun onClick(items: ArrayList<BluetoothDevice>, position: Int) {
                Log.d("Log_Item_Click",items.toString())
                val device = items[position]
//                bluetoothGatt = device.connectGatt(this,false,)
//                val intent = Intent(this@BLEConnectActivity, DeviceControlActivity::class.java)
//                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.name)
//                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.address)
                if (mScanning) {
                    bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
                    mScanning = false
                }
                val intent = Intent()
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.name)
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.address)
                setResult(RESULT_OK,intent)
                finish()
//                startActivity(intent)
            }
        }
        mLeDeviceAdapter = LeDeviceAdapter(listener, arrayDevices)
        recyclerView_Scan.adapter = mLeDeviceAdapter

        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            Toast.makeText(this, "This Device BLE Not Supported!", Toast.LENGTH_SHORT).show()
            finish()
        }

        scanLeDevice(true)
    }

    override fun onResume() {
        super.onResume()

//         Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!bluetoothAdapter.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(
                enableBtIntent,
                REQUEST_ENABLE_BT
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val mf = menuInflater
        mf.inflate(R.menu.device_scan_menu, menu)
        menu_stop = menu.findItem(R.id.menu_stop)
        menu_scan = menu.findItem(R.id.menu_scan)
        menu_refresh = menu.findItem(R.id.menu_refresh)
        menu_refresh.setActionView(R.layout.actionbar_indeterminate_progress)
        menu_scan.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_scan -> {
                mLeDeviceAdapter.clear()
                scanLeDevice(true)
                menu_stop.isVisible = true
                menu_scan.isVisible = false
                menu_refresh.setActionView(R.layout.actionbar_indeterminate_progress)
                menu_refresh.isVisible=true
            }
            R.id.menu_stop ->{
                scanLeDevice(false)
                menu_stop.isVisible = true
                menu_scan.isVisible = true
                menu_refresh.isVisible = false
            }
        }
        return true
    }

    //장치 스캔
    //1. 원하는 장치를 찾으면 검색 중지
    //2. 반드시 검색 시간 제한 설정 (액티비티를 벗어나도 스캔이 계속될 수 도 있기 때문)
    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                handler.postDelayed({
                    mScanning = false
                    bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
                }, SCAN_PERIOD)

                mScanning = true
                arrayDevices.clear()
                bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
            }
            else -> {
                mScanning = false
                bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        scanLeDevice(false)
        mLeDeviceAdapter.clear()
    }
}
