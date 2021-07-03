package com.plasdor.app.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.AddressSelectCallback
import com.plasdor.app.model.BazarListItems
import kotlinx.android.synthetic.main.adapter_item_bazar_list_item.view.*

class BazarListAdapter(
    activityContext: Context
) :
    RecyclerView.Adapter<BazarListAdapter.MyViewHolder>() {

    private val listItems = ArrayList<BazarListItems>()
    var context: Context = activityContext
    var selectedPosition = -1

    fun updateListItems(listModel: ArrayList<BazarListItems>) {
        listItems.clear()
        listItems.addAll(listModel)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_item_bazar_list_item, parent, false)

        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: BazarListItems = listItems.get(position)

        holder.txtProductName.text = item.productName
        holder.txtFreePoints.text = item.forFreePoints
        holder.txtOneDayPoint.text = item.oneDayPoints
        holder.txt3DayPoint.text = item.threeDayPoints
        holder.txt5DayPoint.text = item.fiveDayPoints

    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtProductName = view.txtProductName
        var txtFreePoints = view.txtFreePoints
        var txtOneDayPoint = view.txtOneDayPoint
        var txt3DayPoint = view.txt3DayPoint
        var txt5DayPoint = view.txt5DayPoint


        val cardView: View = itemView

    }
}