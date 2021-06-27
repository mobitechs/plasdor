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
    activityContext: Context,
    val userType: String
) :
    RecyclerView.Adapter<UserOrderListAdapter.MyViewHolder>(), ApiResponse {

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
        holder.txtOrderId.text = item.orderId
        holder.txtOrderAmount.text = "Rs." + item.totalPrice
        //holder.status = item.status
        holder.txtOrderStatus.text = holder.status
        holder.txtProductDetails.text  = item.productName //+" Type "+item.type

        var orderDate = parseDateToddMMyyyy(item.addedDate)
        holder.txtOrderDate.text = orderDate
//        holder.txtOrderDetails.text = item.orderDetails


        holder.itemView.setOnClickListener {
//            context.openActivity(ProductDetailsFragment::class.java)
//            {
//                putParcelable("OrderDetails", item)
//            }

            var bundle = Bundle()
            bundle.putParcelable("OrderDetails", item)
            (context as UserHomeActivity?)!!.OpenOrderDetails(bundle)
        }


        if (userType.equals(Constants.ADMIN)) {
            holder.txtOrderStatus.visibility = View.GONE
            holder.spinner.visibility = View.VISIBLE
            val adapter = ArrayAdapter(
                context,
                R.layout.spinner_layout,
                Constants.orderStatusArray
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spinner.setAdapter(adapter)

            holder.spinner.setSelection(Constants.orderStatusArray.indexOf(holder.status))
            holder.spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var status = Constants.orderStatusArray[p2]
                    if (!holder.status.equals(status)) {
                        holder.status = status
//                        listItems[position].status = status
                        callAPIToChangeOrderStatus(item.orderId, status)
                    }

                }
            })
        } else {
            holder.txtOrderStatus.visibility = View.VISIBLE
            holder.spinner.visibility = View.GONE
        }
    }

    private fun callAPIToChangeOrderStatus(orderId: String, status: String) {
        val method = "ChangeOrderStatus"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("orderId", orderId)
            jsonObject.put("orderStatus", status)
            jsonObject.put("clientBusinessId", Constants.clientBusinessId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtOrderId: TextView = view.findViewById(R.id.txtOrderId)
        var txtOrderAmount: TextView = view.findViewById(R.id.txtOrderAmount)
        var txtOrderStatus: TextView = view.findViewById(R.id.txtOrderStatus)
        var txtOrderDate: TextView = view.findViewById(R.id.txtOrderDate)
        var spinner: AppCompatSpinner = view.findViewById(R.id.spinner)
        var txtProductDetails: AppCompatTextView = view.findViewById(R.id.txtProductDetails)

        var status = ""

        val cardView: View = itemView

    }

    override fun onSuccess(data: Any, tag: String) {
        if (data.equals("SUCCESS")) {
            context.showToastMsg("Order status change successfully.")
        } else {
            context.showToastMsg("Failed to change order status.")
        }
    }

    override fun onFailure(message: String) {
        context.showToastMsg(message)
    }
}