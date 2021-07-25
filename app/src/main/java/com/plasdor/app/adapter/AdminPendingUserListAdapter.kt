package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.model.UserModel
import com.plasdor.app.utils.ThreeTwoImageView
import com.plasdor.app.view.activity.AdminHomeActivity

class AdminPendingUserListAdapter(
    activityContext: Context
) :
    RecyclerView.Adapter<AdminPendingUserListAdapter.MyViewHolder>() {

    private var allListItems = ArrayList<UserModel>()
    private var listItems = ArrayList<UserModel>()
    var context: Context = activityContext

    lateinit var adapter: ArrayAdapter<String>
    var selectedPosition = -1


    fun updateListItems(items: ArrayList<UserModel>) {
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

        var item: UserModel = listItems.get(position)

        holder.txtName.text = item.name
        holder.txtMobile.text = item.mobile
        holder.txtEmail.text = item.email
        holder.txtAddress.text = item.address
        holder.txtCity.text = item.city
        holder.txtPinCode.text = item.pincode
        holder.txtUserType.text = item.userType

        if(item.isVerified == "2"){
            //blocked
            holder.txtVerification.text = "Verification Blocked"
            holder.txtVerification.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
        else{
            //pending
            holder.txtVerification.text = "Verification Pending"
            holder.txtVerification.setTextColor(ContextCompat.getColor(context, R.color.green))
        }

        holder.imgRadioBtn.visibility = View.GONE
        holder.txtDistance.visibility = View.GONE
        holder.txtMobile.visibility = View.VISIBLE
        holder.txtEmail.visibility = View.VISIBLE
        holder.txtAddress.visibility = View.VISIBLE
        holder.txtCity.visibility = View.VISIBLE
        holder.txtPinCode.visibility = View.VISIBLE
        holder.txtUserType.visibility = View.VISIBLE
        holder.txtVerification.visibility = View.VISIBLE

        holder.itemView.setOnClickListener {

            var bundle = Bundle()
            bundle.putParcelable("userDetails", item)
            (context as AdminHomeActivity?)!!.OpenPendingUserDetails(bundle)
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
        var txtDistance: AppCompatTextView = view.findViewById(R.id.txtDistance)
        var txtUserType: AppCompatTextView = view.findViewById(R.id.txtUserType)
        var txtVerification: AppCompatTextView = view.findViewById(R.id.txtVerification)

        val cardView: View = itemView

    }


    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    listItems = allListItems
                } else {
                    val filteredList: MutableList<UserModel> = ArrayList()
                    for (row in allListItems) {
                        if (row.name.toLowerCase()
                                .contains(charString.toLowerCase()) || row.city.toLowerCase()
                                .contains(charString.toLowerCase()) || row.userType.toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    listItems = filteredList as ArrayList<UserModel>
                }
                val filterResults = FilterResults()
                filterResults.values = listItems
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                listItems = filterResults.values as ArrayList<UserModel>
                notifyDataSetChanged()
            }
        }
    }
}