package com.klab.upright

import java.util.*

class SampleGattAttributes {
    private val attributes: HashMap<String?, String?> =
        HashMap<String?,String?>()

    init {
        // Sample Services.
        attributes["0000fff0-0000-1000-8000-00805f9b34fb"] = "UnKnown Service"
        // Sample Characteristics.
        attributes["00002902-0000-1000-8000-00805f9b34fb"] = "Unknown Characteristic"
    }

    fun lookup(uuid: String?, defaultName: String?): String? {
        val name = attributes[uuid]
        return name ?: defaultName
    }
}