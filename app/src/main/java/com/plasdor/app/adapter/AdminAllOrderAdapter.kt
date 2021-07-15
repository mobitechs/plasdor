package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.ApiResponse
import com.plasdor.app.model.AdminAllOrderListItems
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.apiPostCall
import com.plasdor.app.utils.parseDateToddMMyyyy
import com.plasdor.app.utils.showToastMsg
import com.plasdor.app.view.activity.AdminHomeActivity
import org.json.JSONException
import org.json.JSONObject

class AdminAllOrderAdapter(
    activityContext: Context,
    private val txtTotal: TextView
) :
    RecyclerView.Adapter<AdminAllOrderAdapter.MyViewHolder>(), ApiResponse {

    private var listItems = ArrayList<AdminAllOrderListItems>()
    private val allListItems = ArrayList<AdminAllOrderListItems>()
    var context: Context = activityContext
    var totalEarnedAmount = 0f

    fun updateListItems(list: ArrayList<AdminAllOrderListItems>) {
        listItems.clear()
        allListItems.clear()
        listItems.addAll(list)
        allListItems.addAll(list)
        notifyDataSetChanged()
        updateTotalAmount()
    }


    private fun updateTotalAmount() {
        for (i in 0..listItems.size - 1) {
            var bAmt = listItems[i].adminWillGet
            if (bAmt.equals("")) {
                bAmt = "0"
            }
            totalEarnedAmount = totalEarnedAmount + bAmt.toFloat()
        }
        txtTotal.text = totalEarnedAmount.toString()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_item_admin_order_list_item, parent, false)

        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: AdminAllOrderListItems = listItems.get(position)
        holder.txtOrderId.text = item.orderId
        holder.txtOrderAmount.text = "Rs." + item.totalPrice

        holder.txtProductDetails.text = item.productName //+ " Type " + item.type
        holder.txtUserName.text = item.name
        holder.txtMerchantName.text = item.merchantName

        var orderDate = parseDateToddMMyyyy(item.addedDate)
        holder.txtOrderDate.text = orderDate
//        holder.txtOrderDetails.text = item.orderDetails

        if(item.paymentType.equals("Redeem")){
            holder.txtRedeemPoint.text = item.redeemPointsUsed+" Points"
            holder.txtRedeemPoint.visibility = View.VISIBLE
            holder.txtOrderAmount.visibility = View.GONE
        }else{
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
            (context as AdminHomeActivity?)!!.OpenOrderDetails(bundle)
        }



        if (item.orderStatus.equals("Delivered")) {
            holder.txtOrderStatus.text = item.orderStatus
            holder.txtOrderStatus.visibility = View.VISIBLE
            holder.spinner.visibility = View.GONE
            holder.txtDeliverBy.visibility = View.GONE

        } else {
            if (item.deliveredBy.equals(Constants.MERCHANT)) {
                holder.txtOrderStatus.visibility = View.VISIBLE
                holder.txtDeliverBy.visibility = View.VISIBLE
                holder.txtDeliverBy.text = "Delivered by Merchant"
                holder.spinner.visibility = View.GONE
            }
            else if (item.deliveredBy.equals(Constants.COMPANY)) {
                holder.txtOrderStatus.visibility = View.GONE
                holder.spinner.visibility = View.VISIBLE
                holder.txtDeliverBy.visibility = View.VISIBLE
                holder.txtDeliverBy.text = "Delivered by You"
            }
            else if (item.deliveredBy.equals(Constants.SELF)) {
                holder.txtOrderStatus.visibility = View.GONE
                holder.spinner.visibility = View.VISIBLE
                holder.txtDeliverBy.visibility = View.VISIBLE
                holder.txtDeliverBy.text = "Self Pickup by User"
            }
        }


        val adapter = ArrayAdapter(
            context,
            R.layout.spinner_layout,
            Constants.orderStatusArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinner.setAdapter(adapter)

        holder.spinner.setSelection(Constants.orderStatusArray.indexOf(item.orderStatus))
        holder.spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var status = Constants.orderStatusArray[p2]
                if (!item.orderStatus.equals(status)) {
//                        listItems[position].status = status
                    callAPIToChangeOrderStatus(item.orderId, status)
                }

            }
        })


    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtOrderId: AppCompatTextView = view.findViewById(R.id.txtOrderId)
        var txtOrderAmount: AppCompatTextView = view.findViewById(R.id.txtOrderAmount)
        var txtOrderStatus: AppCompatTextView = view.findViewById(R.id.txtOrderStatus)
        var txtOrderDate: AppCompatTextView = view.findViewById(R.id.txtOrderDate)
        var spinner: AppCompatSpinner = view.findViewById(R.id.spinner)
        var txtProductDetails: AppCompatTextView = view.findViewById(R.id.txtProductDetails)
        var txtUserName: AppCompatTextView = view.findViewById(R.id.txtUserName)
        var txtMerchantName: AppCompatTextView = view.findViewById(R.id.txtMerchantName)
        var txtDeliverBy: AppCompatTextView = view.findViewById(R.id.txtDeliverBy)
        var txtRedeemPoint: AppCompatTextView = view.findViewById(R.id.txtRedeemPoint)

        val cardView: View = itemView

    }

    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    listItems = allListItems
                } else {
                    val filteredList: MutableList<AdminAllOrderListItems> = ArrayList()
                    for (row in allListItems) {
                        if (row.orderId.toLowerCase().contains(charString.toLowerCase())
                            || row.totalPrice.toLowerCase().contains(charString.toLowerCase())
                            || row.orderStatus.toLowerCase().contains(charString.toLowerCase())
                            || row.addedDate.toLowerCase().contains(charString.toLowerCase())
                            || row.address.toLowerCase().contains(charString.toLowerCase())
                            || row.city.toLowerCase().contains(charString.toLowerCase())
                            || row.productName.toLowerCase().contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    listItems = filteredList as ArrayList<AdminAllOrderListItems>
                }
                val filterResults = FilterResults()
                filterResults.values = listItems
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                listItems = filterResults.values as ArrayList<AdminAllOrderListItems>
                notifyDataSetChanged()
            }
        }
    }

    private fun callAPIToChangeOrderStatus(orderId: String, status: String) {
        val method = "changeOrderStatus"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("orderId", orderId)
            jsonObject.put("orderStatus", status)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
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