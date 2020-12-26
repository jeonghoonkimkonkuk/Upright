package com.klab.upright

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LeDeviceAdapter(
    val clickListener: itemClickListener,
    private val items: ArrayList<BluetoothDevice>
): RecyclerView.Adapter<LeDeviceAdapter.ViewHolder>() {

    lateinit var context: Context

    interface itemClickListener {
        fun onClick(items: ArrayList<BluetoothDevice>, position: Int)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var deviceName: TextView = itemView.findViewById(R.id.device_name)
        var deviceAddress: TextView = itemView.findViewById(R.id.device_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeDeviceAdapter.ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context)
            .inflate(R.layout.item_device, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: LeDeviceAdapter.ViewHolder, position: Int) {
        holder.deviceName.text = items[position].name
        holder.deviceAddress.text = items[position].address
        holder.itemView.setOnClickListener {
            clickListener.onClick(items,position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addDevice(device: BluetoothDevice) {
        if (!items.contains(device)) {
            items.add(device)
        }
    }

    fun getDevice(position: Int): BluetoothDevice {
        return items[position]
    }

    fun clear() {
        items.clear()
    }

}