package com.klab.upright.ui.analysis

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import com.klab.upright.ChartAdapter
import com.klab.upright.ui.memo.MemoData
import kotlinx.android.synthetic.main.custom_tab.*
import kotlinx.android.synthetic.main.fragment_analysis.*
import java.util.*
import kotlin.collections.ArrayList
import com.klab.upright.R
import kotlinx.android.synthetic.main.custom_tab.view.*


class AnalysisFragment : Fragment() {


    lateinit var memoDB: DatabaseReference
    lateinit var timeDB: DatabaseReference

    private lateinit var startDate: Calendar
    private lateinit var endDate: Calendar

    val memoList = ArrayList<MemoData>()
    val timeList = ArrayList<Time>()

    private var start:Int=0
    private var end:Int =0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        initTapLayout()
        initViewPager()
    }

    private fun initTapLayout() {
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView("MY POSTURE")))
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView("WEARING TIME")))
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView("PAIN")))

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.setCurrentItem(tab!!.position)
            }

        })
    }

    private fun initViewPager(){
        val itemList = ArrayList<Time>()
        for (item in timeList) {
            if (item.date in start..end) {
                itemList.add(item)
            }
        }
        val painList = ArrayList<MemoData>()
        for (pain in memoList) {
            if (pain.date in start..end) {
                painList.add(pain)
            }
        }
        viewPager.adapter = ChartAdapter(requireContext(), itemList,painList)
    }

    private fun init() {
        startDate = Calendar.getInstance()
        endDate = Calendar.getInstance()
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = (Calendar.getInstance().get(Calendar.MONTH)+1)
        val day = Calendar.getInstance().get(Calendar.DATE)

        startText.text = "$year.$month.$day"
        start = year*10000+(month)*100+day
        endText.text = "$year.$month.$day"
        end = year*10000+(month)*100+day

        //달력 시작 버튼 클릭
        startDate_pattern.setOnClickListener {
            val datePickerListener =
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    startDate.set(year,month,dayOfMonth)
                    startText.text = "${year}.${month+1}.${dayOfMonth}"
                    start = year*10000+(month+1)*100+dayOfMonth
                    updateMemoData()
                    initViewPager()
                }

            val builder = DatePickerDialog(requireContext(),datePickerListener,startDate.get(Calendar.YEAR),startDate.get(Calendar.MONTH),startDate.get(Calendar.DATE))
            builder.setTitle("statDate")
            builder.show()
        }

        //달력 마지막 버튼 클릭
        endDate_pattern.setOnClickListener {
            val datePickerListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    endDate.set(year,month,dayOfMonth)
                    endText.text = "${year}.${month+1}.${dayOfMonth}"
                    end = year*10000+(month+1)*100+dayOfMonth
                    updateMemoData()
                    initViewPager()
                }

            val builder = DatePickerDialog(requireContext(),datePickerListener,endDate.get(Calendar.YEAR),endDate.get(Calendar.MONTH),endDate.get(Calendar.DATE))
            builder.show()
        }

        memoDB = FirebaseDatabase.getInstance().reference.child("memo")
        memoDB.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                memoList.clear()
                for(item in snapshot.children) {
                    memoList.add(item.getValue(MemoData::class.java)!!)
                }
                updateMemoData()
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })


        timeDB = FirebaseDatabase.getInstance().reference.child("WearingTime")
        timeDB.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                timeList.clear()
                for (item in snapshot.children) {
                    timeList.add(item.getValue(Time::class.java)!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })

    }

    fun updateMemoData() {
        //update data
        var time=0
        var pain=0
        var n=0
        for(memo in memoList) {
            if(memo.date in start..end) {
                time += memo.duration
                pain += memo.pain
                n++
            }
        }
        val pain_avg:Double = pain.toDouble()/n
//        pain_text.text = pain_avg.toString()
//        exercise_text.text = time.toString()+"m"

    }

    private fun createTabView(tabName: String): View? {
        val tabView =
            LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
        tabView.tab_text.text = tabName
        return tabView
    }
}