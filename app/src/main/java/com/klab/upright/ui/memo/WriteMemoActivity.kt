package com.klab.upright.ui.memo

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.github.mikephil.charting.utils.Utils.init
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.klab.upright.R
import com.klab.upright.sharedPreference.PreferenceManager
import kotlinx.android.synthetic.main.activity_write_memo.*
import java.time.format.DateTimeFormatter
import java.util.*

class WriteMemoActivity : AppCompatActivity() {

    lateinit var memoDB:DatabaseReference
    val months=arrayOf("January", "February", "March","April","May","June","July","August","September","October","November","December")
    lateinit var writeDate:Calendar
    lateinit var pref: PreferenceManager
    var duration : Int = 0
    var date : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_memo)
        pref = PreferenceManager(this)
        this.supportActionBar?.hide()
        init()
    }

    fun uploadMemo(data : MemoData){
        memoDB = FirebaseDatabase.getInstance().getReference("memo/")
        val key = memoDB.push().key
        if (key != null) {
            memoDB.child(key).setValue(data)
        }
        Log.d("db",data.toString())
    }
    fun init(){

        writeDate = Calendar.getInstance()

        dateText.text = months[(writeDate.get(Calendar.MONTH))] +" "+writeDate.get(Calendar.DATE).toString()+", "+writeDate.get(Calendar.YEAR).toString()
        var date = writeDate[1]*10000+(writeDate[2]+1)*100+writeDate[5]

        dateLayout.setOnClickListener {
            val datePickerListener = object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    writeDate.set(year,month,dayOfMonth)
                    val temp_str = months[(month)] +" "+ dayOfMonth.toString()+", "+year.toString()
                    dateText.text = temp_str
                    date =  year*10000+(month+1)*100+dayOfMonth
                        //year.toString()+"년 "+(month+1).toString()+"월 "+dayOfMonth.toString()+"일"
                }

            }

            val builder = DatePickerDialog(this,datePickerListener,writeDate.get(
                Calendar.YEAR),writeDate.get(Calendar.MONTH),writeDate.get(Calendar.DATE))
            builder.show()
        }

        closeBtn.setOnClickListener {
            finish()
        }

        addBtn.setOnClickListener {
            val time = timeText.text.toString()
            val type = typeText.text.toString()
            val pain = painText.text.toString()
            val edit = contentText.text.toString()
            val data = MemoData(date,dateText.text.toString(),time,type,pain.toInt(),edit,duration)
            pref.addMemo(data)
            uploadMemo(data)

            setResult(RESULT_OK, intent)
            finish()
        }

        timeText.setOnClickListener {
            val dialog1 = SnapTimePickerDialog.Builder().apply {
                //this.setTitle("Please select the exercise time") -> 이거 수정해야함
            }.build().apply {
                setListener{ hour1,minute1 ->

                    this.requireActivity().timeText.text = hour1.toString()+" : "+minute1.toString()

                    val dialog2 = SnapTimePickerDialog.Builder().apply {
                    }.build().apply {
                        setListener{ hour2,minute2 ->
                            val origin = this.requireActivity().timeText.text.toString()
                            this.requireActivity().timeText.text = origin + " ~ " + hour2.toString()+" : "+minute2.toString()

                            duration = (hour2-hour1)*60+minute2-minute1
                        }
                    }

                    dialog2.show(supportFragmentManager,SnapTimePickerDialog.TAG)
                }
            }
            dialog1.show(supportFragmentManager,SnapTimePickerDialog.TAG)
        }
    }
}