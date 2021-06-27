package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.model.AdminAllOrderListItems
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.utils.parseDateToddMMyyyy
import com.plasdor.app.view.activity.AdminHomeActivity
import com.plasdor.app.view.activity.MerchantHomeActivity

class AdminAllOrderAdapter(
    activityContext: Context
) :
    RecyclerView.Adapter<AdminAllOrderAdapter.MyViewHolder>() {

    private var listItems = ArrayList<AdminAllOrderListItems>()
    private val allListItems = ArrayList<AdminAllOrderListItems>()
    var context: Context = activityContext

    fun updateListItems(list: ArrayList<AdminAllOrderListItems>) {
        listItems.clear()
        allListItems.clear()
        listItems.addAll(list)
        allListItems.addAll(list)
        notifyDataSetChanged()
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
        //holder.status = item.status
        holder.txtOrderStatus.text = holder.status
        holder.txtProductDetails.text = item.productName //+ " Type " + item.type
        holder.txtUserName.text = item.name
        holder.txtMerchantName.text = item.merchantName

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
            (context as AdminHomeActivity?)!!.OpenOrderDetails(bundle)
        }


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

        var status = ""

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


}