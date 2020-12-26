package com.klab.upright.ui.guide

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.klab.upright.R
import com.klab.upright.sharedPreference.PreferenceManager
import kotlinx.android.synthetic.main.item_guide5.view.*

class GuideViewPagerAdapter(private val context: Context): PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null
    var pref: PreferenceManager = PreferenceManager(context)

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 4
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater!!.inflate(R.layout.viewpager_guide, null)
//        val image = v.findViewById<ImageView>(R.id.imageView_guide)
        val layout = v.findViewById<LinearLayout>(R.id.layout)


        when(position){
            0->{
                val child = (layoutInflater!!.inflate(R.layout.item_guide1, null)).findViewById<LinearLayout>(R.id.guide1)
                child.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                layout.addView(child)
            }
            1->{
                val child = (layoutInflater!!.inflate(R.layout.item_guide2, null)).findViewById<LinearLayout>(R.id.guide2)
                child.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                layout.addView(child)
            }
            2->{
                val child = (layoutInflater!!.inflate(R.layout.item_guide3, null)).findViewById<LinearLayout>(R.id.guide3)
                child.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                layout.addView(child)
            }
            else->{
                val child = (layoutInflater!!.inflate(R.layout.item_guide5, null)).findViewById<LinearLayout>(R.id.guide5)
                child.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                child.saveBtn.setOnClickListener {
                    if(child.userName.text.isNotEmpty()){
                        pref.setUserName(child.userName.text.toString())
                        Toast.makeText(context,"your name is "+pref.getUserName(),Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"please write your name !",Toast.LENGTH_SHORT).show()
                    }
                }
                layout.addView(child)
            }

        }
        val vp = container as ViewPager
        vp.addView(v, 0)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val v = `object` as View
        vp.removeView(v)
    }


}