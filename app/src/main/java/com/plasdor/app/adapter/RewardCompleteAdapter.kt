package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import kotlinx.android.synthetic.main.adapter_item_reward_complete.view.*

class RewardCompleteAdapter (
    activityContext: Context,
    val listItems: ArrayList<String>,
    val count: Int,
) :
    RecyclerView.Adapter<RewardCompleteAdapter.MyViewHolder>() {


    var context: Context = activityContext
    var selectedPosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_item_reward_complete, parent, false)

        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: String = listItems.get(position)

        if(position < count){
            //green
            holder.layoutForBg.getBackground().setColorFilter(
                ContextCompat.getColor(context,R.color.green),
                PorterDuff.Mode.SRC_ATOP
            )
        }else{
            //gray
            holder.layoutForBg.getBackground().setColorFilter(
                ContextCompat.getColor(context,R.color.border),
                PorterDuff.Mode.SRC_ATOP
            )
        }

//        holder.layoutForBg.text = item.productName
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layoutForBg = view.layoutForBg
        val cardView: View = itemView

    }
}