package com.klab.upright.ui.analysis

import java.io.Serializable

data class Time(val c1: Int,
                val c10: Int,
                val c11: Int,
                val c12: Int,
                val c13: Int,
                val c14: Int,
                val c15: Int,
                val c2: Int,
                val c3: Int,
                val c4: Int,
                val c5: Int,
                val c6: Int,
                val c7: Int,
                val c8: Int,
                val c9: Int,
                val date: Int,
                val total: Int) : Serializable {
    constructor():this(0,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0, 0, 99999999, 99999)
}