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
import java.util.HashMap

class DataListAdapter(
    val context: Context,
    val clickListener: DataListAdapter.itemClickListener,
    val gattServiceData: ArrayList<HashMap<String, String>>,
    val gattCharacteristicData: ArrayList<ArrayList<HashMap<String, String>>>
): RecyclerView.Adapter<DataListAdapter.ViewHolder>() {


    interface itemClickListener {
//        fun onClick(items: ArrayList<BluetoothDevice>, position: Int)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var dataService: TextView = itemView.findViewById(R.id.data_service)
        var dataChar: TextView = itemView.findViewById(R.id.data_characteristic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataListAdapter.ViewHolder {
        val v = LayoutInflater.from(context)
            .inflate(R.layout.item_data, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataListAdapter.ViewHolder, position: Int) {
        holder.dataService.text = gattServiceData[position].toString()
        holder.dataChar.text = gattCharacteristicData[position].toString()
        holder.itemView.setOnClickListener {
//            clickListener.onClick(items,position)
        }
    }

    override fun getItemCount(): Int {
        return gattServiceData.size
    }
//
//    fun addDevice(device: BluetoothDevice) {
//        if (!items.contains(device)) {
//            items.add(device)
//        }
//    }
//
//    fun getDevice(position: Int): BluetoothDevice {
//        return items[position]
//    }

//    fun clear() {
//        items.clear()
//    }

}