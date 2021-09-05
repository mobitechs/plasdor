package com.plasdor.app.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.AddOrRemoveListener
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.setImage
import kotlinx.android.synthetic.main.adapter_item_bazar_list_item.view.*

class BazarListAdapter(
    activityContext: Context,
    private val addOrRemoveListener: AddOrRemoveListener,
    val firstFreeOrder: String,
) :
    RecyclerView.Adapter<BazarListAdapter.MyViewHolder>() {

    private val listItems = ArrayList<ProductListItems>()
    var context: Context = activityContext
    var selectedPosition = -1

    fun updateListItems(listModel: ArrayList<ProductListItems>) {
        listItems.clear()
        listItems.addAll(listModel)
        var tempItem = listModel
        if (firstFreeOrder.equals("1")) {
            //remove
            for (i in 0..listItems.size - 1) {
                var itemNO = listItems[i]
                if (listItems[i].reason.equals(Constants.First_Order)) {
                    tempItem.remove(itemNO)
                }
            }
            listItems.clear()
            listItems.addAll(tempItem)
        }



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

        var item: ProductListItems = listItems.get(position)

        holder.txtProductName.text = item.productName
        holder.txtFreePoints.text = item.forFreePoints+" Points"
        holder.txtOneDayPoint.text = item.oneDayPoints+" Points"
        holder.txt3DayPoint.text = item.threeDayPoints+" Points"
        holder.txt5DayPoint.text = item.fiveDayPoints+" Points"
        holder.txtReason.text = item.reason
        holder.txtPoints.text = item.points+" Points"

        val imagepath = item.img
        if (imagepath == null || imagepath == " ") {
            holder.productImage!!.background =
                context.resources.getDrawable(R.drawable.img_not_available)

        } else {
            holder.productImage!!.setImage(imagepath)

        }

        holder.itemView.setOnClickListener {
            addOrRemoveListener.selectProduct(item, position)
        }
        holder.btnPlaceFreeOrder.setOnClickListener {
            addOrRemoveListener.selectProduct(item, position)
        }


        if(firstFreeOrder.equals("0") && item.reason.equals(Constants.First_Order)){
//            holder.layoutForPaid.visibility = View.GONE
            holder.layoutForPaid2.visibility = View.GONE
            holder.layoutForFree.visibility = View.VISIBLE
        }else{
            holder.layoutForFree.visibility = View.GONE
//            holder.layoutForPaid.visibility = View.VISIBLE
            holder.layoutForPaid2.visibility = View.VISIBLE
        }

    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtProductName = view.txtProductName
        var txtFreePoints = view.txtFreePoints
        var txtOneDayPoint = view.txtOneDayPoint
        var txt3DayPoint = view.txt3DayPoint
        var txt5DayPoint = view.txt5DayPoint
        var productImage = view.productImage
        var layoutForFree = view.layoutForFree
        var layoutForPaid = view.layoutForPaid
        var layoutForPaid2 = view.layoutForPaid2
        var btnPlaceFreeOrder = view.btnPlaceFreeOrder
        var txtReason = view.txtReason
        var txtPoints = view.txtPoints


        val cardView: View = itemView

    }
}