package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.MyOrderListItems
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.parseDateToddMMyyyy
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.UserHomeActivity
import org.json.JSONException
import org.json.JSONObject

class UserOrderListAdapter(
    activityContext: Context
) :
    RecyclerView.Adapter<UserOrderListAdapter.MyViewHolder>() {

    private val listItems = ArrayList<MyOrderListItems>()
    var context: Context = activityContext

    fun updateListItems(categoryModel: ArrayList<MyOrderListItems>) {
        listItems.clear()
        listItems.addAll(categoryModel)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_list_items_user_order, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: MyOrderListItems = listItems.get(position)
        holder.txtOrderId.text = Constants.Order_ID_INITIAL+ item.orderId
        holder.txtOrderAmount.text = "Rs." + item.totalPrice
        //holder.status = item.status
        holder.txtOrderStatus.text = holder.status
        holder.txtProductDetails.text  = item.productName //+" Type "+item.type

        var orderDate = parseDateToddMMyyyy(item.addedDate)
        holder.txtOrderDate.text = orderDate
        holder.txtDeliveryStatus.text = item.orderStatus

//        holder.txtOrderDetails.text = item.orderDetails

        if(item.paymentType.equals("Redeem")){
            holder.txtRedeemPoint.text = item.redeemPointsUsed+" Points"
            holder.txtRedeemPoint.visibility = View.VISIBLE
            holder.txtOrderAmount.visibility = View.GONE
        }
        else{
            holder.txtRedeemPoint.visibility = View.GONE
            holder.txtOrderAmount.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
//            context.openActivity(ProductDetailsFragment::class.java)
//            {
//                putParcelable("OrderDetails", item)
//            }

            var bundle = Bundle()
            bundle.putParcelable("OrderDetails", item)
            (context as UserHomeActivity?)!!.OpenOrderDetails(bundle)
        }


    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtOrderId: TextView = view.findViewById(R.id.txtOrderId)
        var txtOrderAmount: TextView = view.findViewById(R.id.txtOrderAmount)
        var txtOrderStatus: TextView = view.findViewById(R.id.txtOrderStatus)
        var txtOrderDate: TextView = view.findViewById(R.id.txtOrderDate)
        var spinner: AppCompatSpinner = view.findViewById(R.id.spinner)
        var txtDeliveryStatus: AppCompatTextView = view.findViewById(R.id.txtDeliveryStatus)
        var txtProductDetails: AppCompatTextView = view.findViewById(R.id.txtProductDetails)
        var txtRedeemPoint: AppCompatTextView = view.findViewById(R.id.txtRedeemPoint)

        var status = ""
        val cardView: View = itemView

    }

}