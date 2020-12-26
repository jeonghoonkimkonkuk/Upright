package com.klab.upright

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.klab.upright.ui.analysis.Time
import com.klab.upright.ui.memo.MemoData
import kotlinx.android.synthetic.main.layout_chart.view.*
import kotlinx.android.synthetic.main.layout_chart2.view.*
import kotlinx.android.synthetic.main.layout_chart3.view.*
import com.github.mikephil.charting.formatter.ValueFormatter as ValueFormatter1


class ChartAdapter(val context:Context, val itemList: ArrayList<Time>, val memoList:ArrayList<MemoData>) : PagerAdapter()
{
    val TAG = "ChartAdapter"

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 3
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val view: View
        when(position){
            0->{ // 클래스별 시간
                var classList = arrayListOf<Int>(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)
                var nameList1 = arrayListOf<String>("Very Backward","Backward","Balanced","Forward","Very Forward")
                var nameList2 = arrayListOf<String>("Left","Balanced","Right")
                for(item in itemList){
                    classList[0] += item.c1
                    classList[1] += item.c2
                    classList[2] += item.c3
                    classList[3] += item.c4
                    classList[4] += item.c5
                    classList[5] += item.c6
                    classList[6] += item.c7
                    classList[7] += item.c8
                    classList[8] += item.c9
                    classList[9] += item.c10
                    classList[10] += item.c11
                    classList[11] += item.c12
                    classList[12] += item.c13
                    classList[13] += item.c14
                    classList[14] += item.c15
                }
                var total = 0
                for(num in classList)
                    total+=num
                val entries = arrayListOf<PieEntry>()

                view = inflater.inflate(R.layout.layout_chart2,null)
                val pieChart = view.pieChart1
                pieChart.apply {
                    isDrawHoleEnabled = true
                    setHoleColor(Color.WHITE)
                    setTransparentCircleAlpha(110);

                    holeRadius = 55f
                    transparentCircleRadius = 65f
                    setDrawCenterText(true);
                    rotationAngle = 0f
                    // enable rotation of the chart by touch
                    isRotationEnabled = false
                    isHighlightPerTapEnabled = true

                    highlightValues(null)
                    invalidate()
                    description = null
                    legend.isEnabled = false
                    centerText = "Vertical"
                    setCenterTextSize(17f)
                    animateY(1400, Easing.EaseInOutQuad)
                    setEntryLabelColor(ContextCompat.getColor(context,R.color.level9_blue))
                }


                for (i in 0 until 5) {
                    entries.add(
                        PieEntry(
                            (classList[i*3]+classList[i*3+1]+classList[i*3+2]).toFloat(),
                            nameList1[i]
                        )
                    )
                }

                val dataSet = PieDataSet(entries, "Election Results")

                dataSet.setDrawIcons(false)

                dataSet.sliceSpace = 5f
                dataSet.iconsOffset = MPPointF(0f, 40f)
                dataSet.selectionShift = 5f

                val colors = ArrayList<Int>()
                colors.add(ContextCompat.getColor(context,R.color.level1_blue))
                colors.add(ContextCompat.getColor(context,R.color.level2_blue))
                colors.add(ContextCompat.getColor(context,R.color.level3_blue))
                colors.add(ContextCompat.getColor(context,R.color.level4_blue))
                colors.add(ContextCompat.getColor(context,R.color.level5_blue))
                colors.add(ContextCompat.getColor(context,R.color.level6_blue))
                colors.add(ContextCompat.getColor(context,R.color.level7_blue))
                colors.add(ContextCompat.getColor(context,R.color.level8_blue))
                colors.add(ContextCompat.getColor(context,R.color.level9_blue))

                dataSet.colors = colors

                val data = PieData(dataSet)
                data.apply {
                    setValueTextSize(15f)
                    setValueTextColor(ContextCompat.getColor(context,R.color.level9_blue))
                    setValueFormatter(object : ValueFormatter1(){
                        override fun getFormattedValue(value: Float): String {
                            return ((value/total)*100).toInt().toString()+"%"
                        }
                    })
                }
                pieChart.data = data


                val pieChart2 = view.pieChart2
                pieChart2.apply {
                    isDrawHoleEnabled = true
                    setHoleColor(Color.WHITE)
//                    setTransparentCircleColor(ContextCompat.getColor(context,R.color.level9_green))
                    setTransparentCircleAlpha(110);

                    holeRadius = 55f
                    transparentCircleRadius = 65f
                    setDrawCenterText(true);
                    rotationAngle = 0f
                    // enable rotation of the chart by touch
                    isRotationEnabled = false
                    isHighlightPerTapEnabled = true

                    highlightValues(null)
                    invalidate()
                    description = null
                    legend.isEnabled = false
                    centerText = "Horizontal"
                    setCenterTextSize(17f)
                    animateY(1400, Easing.EaseInOutQuad)
                    setEntryLabelColor(ContextCompat.getColor(context,R.color.level9_green))
                }

                val entries2 = arrayListOf<PieEntry>()
                for (i in 0 until 3) {
                    entries2.add(
                        PieEntry(
                            (classList[i%3]+classList[i%3+3]+classList[i%3+6]+classList[i%3+9]+classList[i%3+12]).toFloat(),
                            nameList2[i]
                        )
                    )
                }

                val dataSet2 = PieDataSet(entries2, "Election Results")

                dataSet2.setDrawIcons(false)
                dataSet2.sliceSpace = 5f
                dataSet2.iconsOffset = MPPointF(0f, 40f)
                dataSet2.selectionShift = 5f

                val colors2 = ArrayList<Int>()
                colors2.add(ContextCompat.getColor(context,R.color.level1_green))
                colors2.add(ContextCompat.getColor(context,R.color.level2_green))
                colors2.add(ContextCompat.getColor(context,R.color.level3_green))
                colors2.add(ContextCompat.getColor(context,R.color.level4_green))
                colors2.add(ContextCompat.getColor(context,R.color.level5_green))
                colors2.add(ContextCompat.getColor(context,R.color.level6_green))
                colors2.add(ContextCompat.getColor(context,R.color.level7_green))
                colors2.add(ContextCompat.getColor(context,R.color.level8_green))
                colors2.add(ContextCompat.getColor(context,R.color.level9_green))

                dataSet2.colors = colors2

                val data2 = PieData(dataSet2)
                data2.apply {
                    setValueTextSize(15f)
                    setValueTextColor(ContextCompat.getColor(context,R.color.level9_green))
                    setValueFormatter(object : ValueFormatter1(){
                        override fun getFormattedValue(value: Float): String {
                            return ((value/total)*100).toInt().toString()+"%"
                        }
                    })
                }
                pieChart2.data = data2

            }
            1->{ // 날짜별 착용 시간
                val dateList = arrayListOf<String>()
                val totalList = arrayListOf<Int>()
                for(time in itemList){
                    totalList.add(time.total);
                    val str = time.date.toString().substring(4,6)+"/"+time.date.toString().substring(6,8) // 11/22
                    dateList.add(str);
                }

                view = inflater.inflate(R.layout.layout_chart,null)
                val lineChart = view.lineChart
                lineChart.apply {
                    setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                    axisRight.isEnabled=false
                    axisLeft.isEnabled=false
                    axisLeft.setDrawGridLines(false)
                    axisRight.setDrawGridLines(false)
                    description.isEnabled = false
                    setTouchEnabled(false)
                    isDragEnabled = false
                    legend.isEnabled = false
                    animateY(1500)
                }


                val xAxis = lineChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.isGranularityEnabled = true
                xAxis.textSize = 13f

                val yAxis = lineChart.axisLeft
                yAxis.setDrawGridLines(false)

                val values = arrayListOf<Entry>()

                for(i in totalList.indices){
                    values.add(Entry(i.toFloat(),totalList[i].toFloat(),ContextCompat.getDrawable(context, R.drawable.button_shadow_purple)))
                }

                xAxis.valueFormatter = object : ValueFormatter1() {
                    override fun getFormattedValue(value: Float): String {
                        if(dateList.size > value.toInt()){
                            return dateList[value.toInt()]
                        }
                        return ""
                    }
                }

                val set1:LineDataSet
                if (lineChart.data != null &&
                    lineChart.data.dataSetCount > 0
                ) {
                    set1 = lineChart.data.getDataSetByIndex(0) as LineDataSet
                    set1.values = values
                    set1.notifyDataSetChanged()
                    lineChart.data.notifyDataChanged()
                    lineChart.notifyDataSetChanged()
                }else {
                    set1 = LineDataSet(values,"Dataset 1")

                    set1.apply {
                        setDrawIcons(false)

                        // black line and point
                        color = ContextCompat.getColor(context,R.color.level4_purple)
                        setCircleColor(ContextCompat.getColor(context,R.color.level4_purple))


                        lineWidth=2f
                        circleRadius = 6f
                        circleHoleRadius = 4f

                        // customize legend entry
                        formLineWidth = 1f
                        formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                        formSize = 13f

                        setDrawCircleHole(true)

                        valueTextSize = 13f

                        cubicIntensity = 0.2f
                        setMode(LineDataSet.Mode.CUBIC_BEZIER)

                        setDrawFilled(true)
                        fillColor = ContextCompat.getColor(context,R.color.level2_purple)
                        fillFormatter =
                            IFillFormatter { dataSet, dataProvider -> lineChart.axisLeft.axisMinimum }
                    }



                    val dataSets = arrayListOf<ILineDataSet>()
                    dataSets.add(set1)

                    val data = LineData(dataSets)

                    lineChart.data = data

                }
            }
            else->{
                view = inflater.inflate(R.layout.layout_chart3,null)
                val barChart = view.barChart

                val dateList = arrayListOf<String>()
                for(i in memoList.indices){
                    val str = memoList[i].date.toString().substring(4,6)+"/"+memoList[i].date.toString().substring(6,8) // 11/22
                    dateList.add(str);
                }

                barChart.apply {
                    setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                    axisRight.isEnabled=false
                    axisLeft.isEnabled=false
                    axisLeft.setDrawGridLines(false)
                    axisRight.setDrawGridLines(false)
                    description.isEnabled = false
                    setTouchEnabled(false)
                    isDragEnabled = false
                    legend.isEnabled = false
                    animateY(1000)
//                    animateXY(1000,1000)
                }

                val xAxis = barChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.isGranularityEnabled = true
                xAxis.textSize = 13f

                val yAxis = barChart.axisLeft
                yAxis.setDrawGridLines(false)

                val values = arrayListOf<BarEntry>()

                for(i in memoList.indices){
                    values.add(BarEntry(i.toFloat(),memoList[i].pain.toFloat(),ContextCompat.getDrawable(context, R.drawable.button_shadow_purple)))
                }

                xAxis.valueFormatter = object : ValueFormatter1() {
                    override fun getFormattedValue(value: Float): String {
                        if(dateList.size > value.toInt()){
                            return dateList[value.toInt()]
                        }
                        return ""
                    }
                }

                val set1:BarDataSet
                if (barChart.data != null &&
                    barChart.data.dataSetCount > 0
                ) {
                    set1 = barChart.data.getDataSetByIndex(0) as BarDataSet
                    set1.values = values
                    set1.notifyDataSetChanged()
                    barChart.data.notifyDataChanged()
                    barChart.notifyDataSetChanged()
                }else {
                    set1 = BarDataSet(values,"Dataset 1")

                    val colorList = arrayListOf<Int>(
                        ContextCompat.getColor(context,R.color.level1_red),
                        ContextCompat.getColor(context,R.color.level2_red),
                        ContextCompat.getColor(context,R.color.level3_red),
                        ContextCompat.getColor(context,R.color.level4_red),
                        ContextCompat.getColor(context,R.color.level5_red)
                    )

                    set1.apply {
                        setDrawIcons(false)

                        // black line and point
                        //color = ContextCompat.getColor(context,R.color.colorPrimary)

                        // customize legend entry
                        formLineWidth = 1f
                        formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                        formSize = 13f

                        valueTextSize = 13f

                        colors = colorList
                        setValueTextColors(colorList)
                    }


                    val dataSets = arrayListOf<IBarDataSet>()
                    dataSets.add(set1)

                    val data = BarData(dataSets)

                    barChart.data = data

                }

            }
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
    }

    private fun setChartData() {

    }

}