package com.klab.upright.ui.tutorial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.klab.upright.MainActivity
import com.klab.upright.R
import kotlinx.android.synthetic.main.activity_posture.*

class PostureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posture)
        init()

    }

    private fun init(){
        initBtn()
    }

    private fun initBtn(){
        // 메인으로 돌아가기 클릭
        mainBtn.setOnClickListener {
            finish()
        }
        // 바른 자세 측정하기 클릭
        setPostureBtn.setOnClickListener {
            setPosture()
        }
    }

    // 바른 자세 측정하기 함수
    private fun setPosture(){
        // set posture

        Toast.makeText(this,"setPosture",Toast.LENGTH_SHORT).show()
    }
}