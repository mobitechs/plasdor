package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.model.ReferralList

class MyReferralListAdapter(
    activityContext: Context
) :
    RecyclerView.Adapter<MyReferralListAdapter.MyViewHolder>() {

    private var listItems = ArrayList<ReferralList>()
    var context: Context = activityContext

    lateinit var adapter: ArrayAdapter<String>
    var selectedPosition = -1
    var userId = ""

    fun updateListItems(items: ArrayList<ReferralList>) {
        listItems.clear()
        listItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_items_my_referral_list, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: ReferralList = listItems.get(position)

        holder.txtName.text = item.name
        //  holder.txtMobile.text = item.mobile
        holder.txtEmail.text = item.email
//        holder.txtAddress.text = item.address
//        holder.txtCity.text = item.city
//        holder.txtPinCode.text = item.pincode
//        holder.txtDOB.text = "Dob: "+item.dob


    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtName: AppCompatTextView = view.findViewById(R.id.txtName)
        var txtEmail: AppCompatTextView = view.findViewById(R.id.txtEmail)
        var txtMobile: AppCompatTextView = view.findViewById(R.id.txtMobile)
        var txtAddress: AppCompatTextView = view.findViewById(R.id.txtAddress)
        var txtCity: AppCompatTextView = view.findViewById(R.id.txtCity)
        var txtPinCode: AppCompatTextView = view.findViewById(R.id.txtPinCode)


        val cardView: View = itemView

    }


}