package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.MerchantProductClickListener
import com.plasdor.app.callbacks.MerchantSelectionClickListener
import com.plasdor.app.model.AvailableMerchantListItem
import com.plasdor.app.utils.ThreeTwoImageView

class AvailableMerchantListAdapter(
    activityContext: Context,
    private val merchantSelectionClickListener: MerchantSelectionClickListener
) :
    RecyclerView.Adapter<AvailableMerchantListAdapter.MyViewHolder>() {

    private var allListItems = ArrayList<AvailableMerchantListItem>()
    private var listItems = ArrayList<AvailableMerchantListItem>()
    var context: Context = activityContext

    lateinit var adapter: ArrayAdapter<String>
    var selectedPosition = -1


    fun updateListItems(items: ArrayList<AvailableMerchantListItem>) {
        listItems.clear()
        listItems.addAll(items)
        allListItems.clear()
        allListItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adpater_item_merchant_available, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: AvailableMerchantListItem = listItems.get(position)

        holder.txtName.text = item.name
        holder.txtMobile.text = item.mobile
        holder.txtEmail.text = item.email
        holder.txtAddress.text = item.address
        holder.txtCity.text = item.city
        holder.txtPinCode.text = item.pincode
        holder.txtRemainingControllerQty.text = "Remaining \nController Qty: "+item.remainingControllerQty


        if (selectedPosition == position)
            holder.imgRadioBtn.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
        else
            holder.imgRadioBtn.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)


        holder.itemView.setOnClickListener {
            holder.imgRadioBtn.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
            selectedPosition = position
            notifyDataSetChanged()
            merchantSelectionClickListener.selectMerchant(item, position)
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageLoader: ProgressBar? = view.findViewById(R.id.progressBar)
        var productImage: ThreeTwoImageView? = view.findViewById(R.id.productImage)
        var txtName: AppCompatTextView = view.findViewById(R.id.txtName)
        var txtMobile: AppCompatTextView = view.findViewById(R.id.txtMobile)
        var txtEmail: AppCompatTextView = view.findViewById(R.id.txtEmail)
        var txtAddress: AppCompatTextView = view.findViewById(R.id.txtAddress)
        var txtCity: AppCompatTextView = view.findViewById(R.id.txtCity)
        var txtPinCode: AppCompatTextView = view.findViewById(R.id.txtPinCode)
        var imgRadioBtn: AppCompatImageView = view.findViewById(R.id.imgRadioBtn)
        var txtRemainingControllerQty: AppCompatTextView = view.findViewById(R.id.txtRemainingControllerQty)

        val cardView: View = itemView

    }


    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    listItems = allListItems
                } else {
                    val filteredList: MutableList<AvailableMerchantListItem> = ArrayList()
                    for (row in allListItems) {
                        if (row.name.toLowerCase()
                                .contains(charString.toLowerCase()) || row.city.toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    listItems = filteredList as ArrayList<AvailableMerchantListItem>
                }
                val filterResults = FilterResults()
                filterResults.values = listItems
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                listItems = filterResults.values as ArrayList<AvailableMerchantListItem>
                notifyDataSetChanged()
            }
        }
    }
}