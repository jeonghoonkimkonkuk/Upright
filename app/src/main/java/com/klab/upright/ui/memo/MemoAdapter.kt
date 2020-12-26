package com.klab.upright.ui.memo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.klab.upright.R
import java.util.*
import kotlin.collections.ArrayList

class MemoAdapter(val context: Context) : RecyclerView.Adapter<MemoAdapter.ViewHolder>(){

    val months=arrayOf("January", "February", "March","April","May","June","July","August","September","October","November","December")
    val memoList = ArrayList<MemoData>()


    init{
            FirebaseDatabase.getInstance().getReference().child("memo").addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    memoList.clear()

                    for(item in snapshot.children){
                        memoList.add(item.getValue(MemoData::class.java)!!)
                    }

                    notifyDataSetChanged()

                    //TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO("Not yet implemented")
                }

            })


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_write,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun onBindViewHolder(holder: MemoAdapter.ViewHolder, position: Int) {
        val writeData = memoList[position]
        val date = writeData.date

        holder.date.text = writeData.date.toString()
        holder.date.text = writeData.dateStr
        holder.time.text = writeData.time
        holder.type.text = writeData.type
        holder.content.text = writeData.content
        //pain
        val pain = writeData.pain
        holder.pain.text = pain.toString()
        when(pain){
            1->holder.faceImg.background = ContextCompat.getDrawable(context,R.drawable.face_level_1)
            2->holder.faceImg.background = ContextCompat.getDrawable(context,R.drawable.face_level_2)
            3->holder.faceImg.background = ContextCompat.getDrawable(context,R.drawable.face_level_3)
            4->holder.faceImg.background = ContextCompat.getDrawable(context,R.drawable.face_level_4)
            else->holder.faceImg.background = ContextCompat.getDrawable(context,R.drawable.face_level_5)
        }

        holder.titleView.setOnClickListener {
            if(holder.detailView.visibility == GONE)
                holder.detailView.visibility = VISIBLE
            else{
                holder.detailView.visibility = GONE
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var date:TextView
        var time:TextView
        var type:TextView
        var pain:TextView
        var content:TextView
        var titleView:LinearLayout
        var detailView:LinearLayout
        var faceImg: ImageView

        init{
            date = itemView.findViewById(R.id.dateText)
            time = itemView.findViewById(R.id.timeText)
            type = itemView.findViewById(R.id.typeText)
            pain = itemView.findViewById(R.id.painText)
            content = itemView.findViewById(R.id.contentText)
            titleView =itemView.findViewById(R.id.titleView)
            detailView =itemView.findViewById(R.id.detailView)
            faceImg =itemView.findViewById(R.id.face_img)

        }

    }


}