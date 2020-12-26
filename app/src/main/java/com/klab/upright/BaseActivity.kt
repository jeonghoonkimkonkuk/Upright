package com.klab.upright

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import com.klab.upright.databinding.DrawerMainBinding
import com.klab.upright.sharedPreference.PreferenceManager

open class BaseActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
//    private lateinit var viewBinding: DrawerMainBinding
    private var currentTheme = R.style.Theme_App_Medium
    private lateinit var pref: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = PreferenceManager(this)
        val textSize = pref.getFontSize()
        currentTheme = getAppTheme(textSize)
        setTheme(currentTheme)

//        viewBinding = DrawerMainBinding.inflate(layoutInflater)
//        setContentView(viewBinding.root)
    }

    override fun onResume() {
        super.onResume()
        val textSize = pref.getFontSize()
        val settingTheme = getAppTheme(textSize)
        if (currentTheme != settingTheme) {
            recreate()
        }
    }

    private fun getAppTheme(textSize: Int) = when (textSize) {
        0 -> R.style.Theme_App_Small
        1 -> R.style.Theme_App_Medium
        2 -> R.style.Theme_App_Large
        else -> R.style.Theme_App_Medium
    }
}