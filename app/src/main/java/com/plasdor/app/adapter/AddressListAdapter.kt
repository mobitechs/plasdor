package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.AddressSelectCallback
import com.plasdor.app.model.AddressListItems
import kotlinx.android.synthetic.main.adapter_items_address.view.*

class AddressListAdapter(
    activityContext: Context,
    val addressSelectCallback: AddressSelectCallback
) :
    RecyclerView.Adapter<AddressListAdapter.MyViewHolder>() {

    private val listItems = ArrayList<AddressListItems>()
    var context: Context = activityContext
    var selectedPosition = -1

    fun updateListItems(listModel: ArrayList<AddressListItems>) {
        listItems.clear()
        listItems.addAll(listModel)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_items_address, parent, false)

        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: AddressListItems = listItems.get(position)

        holder.txtAddress.text = item.address
        holder.txtLandMark.text = item.landMark
        holder.txtArea.text = item.area
        holder.txtCity.text = item.city
        holder.txtPincode.text = item.pincode

        if (selectedPosition == position)
            holder.imgRadioBtn.setImageResource(R.drawable.ic_baseline_radio_button_checked_24)
        else
            holder.imgRadioBtn.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)


        holder.itemView.setOnClickListener {
            addressSelectCallback.selectedAddress(item)
            selectedPosition = position
            notifyDataSetChanged()
        }
        holder.imgDelete.setOnClickListener {
            addressSelectCallback.selectedAddressToDelete(item, position)
            listItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listItems!!.size)
            notifyDataSetChanged()
        }
        holder.imgEdit.setOnClickListener {
            addressSelectCallback.selectedAddressToEdit(item)
        }

    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgRadioBtn = view.imgRadioBtn
        var txtAddress = view.txtAddress
        var txtLandMark = view.txtLandMark
        var txtArea = view.txtArea
        var txtCity = view.txtCity
        var txtPincode = view.txtPincode
        var imgDelete = view.imgDelete
        var imgEdit = view.imgEdit


        val cardView: View = itemView

    }
}