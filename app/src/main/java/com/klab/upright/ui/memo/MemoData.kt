package com.klab.upright.ui.memo

import java.io.Serializable
import java.util.*

class MemoData(val date: Int, val dateStr:String,val time:String, val type:String, val pain:Int, val content:String, val duration:Int) : Serializable{
    constructor():this(0,"null","null","null",0,"null",0)
}