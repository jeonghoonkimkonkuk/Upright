package com.klab.upright.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.klab.upright.data.MyMemoList
import com.klab.upright.ui.memo.MemoData
import java.io.ByteArrayOutputStream

class PreferenceManager(c: Context) {
    val PREFERENCES_NAME = "My_Data"

    var pref: SharedPreferences
    var edit: SharedPreferences.Editor

    init {
        pref = c.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        edit = pref.edit()
    }

    // True or False
    fun setNoADFlag(flag:Boolean){
        edit.putBoolean("no_ad", flag).commit()
    }
    fun getNoADFlag():Boolean {
        return pref.getBoolean("no_ad", false)
    }

    fun setUserName(flag:String){
        edit.putString("user_name",flag).commit()
    }

    fun getUserName(): String? {
        return pref.getString("user_name","noname")
    }

    fun getMemoList():MyMemoList?{
        val gson = Gson()
        val json = pref.getString("memo_list","")
        val data:MyMemoList
        if(json != ""){
            return gson.fromJson(json,MyMemoList::class.java)
        }else{
            val list =MyMemoList(ArrayList<MemoData>())
            return list
        }
    }

    fun addMemo(memo:MemoData){
        val gson = Gson()
        val json = pref.getString("memo_list","")
        val data:MyMemoList
        if(json != ""){
            data = gson.fromJson(json,MyMemoList::class.java)
        }else{
            data = MyMemoList(ArrayList<MemoData>())
        }
        data.memoList.add(memo)
        val str = gson.toJson(data)
        edit.putString("memo_list",str).commit()
    }

    fun setFontSize(size:Int){
        edit.putString("font_size",size.toString()).commit()
    }

    fun getFontSize():Int{
        return (pref.getString("font_size","1")!!).toInt()
    }

}