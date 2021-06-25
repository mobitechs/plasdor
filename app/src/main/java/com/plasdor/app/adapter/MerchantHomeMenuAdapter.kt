package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.MerchantHomeMenuClickListener
import com.plasdor.app.model.HomeMenuItems

class MerchantHomeMenuAdapter(
    activityContext: Context,
    private val merchantClickListener: MerchantHomeMenuClickListener

) :
    RecyclerView.Adapter<MerchantHomeMenuAdapter.MyViewHolder>() {


    private var listItems = ArrayList<HomeMenuItems>()

    var context: Context = activityContext

    lateinit var adapter: ArrayAdapter<String>


    fun updateListItems(items: ArrayList<HomeMenuItems>) {

        listItems.clear()
        listItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.merchant_adapter_home_menu_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: HomeMenuItems = listItems.get(position)

        holder.txtMenuName.text = item.menuName
        holder.imgMenu.setImageResource(item.img)

        holder.itemView.setOnClickListener {
            merchantClickListener.selectProduct(item, position)
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgMenu: AppCompatImageView = view.findViewById(R.id.imgMenu)
        var txtMenuName: TextView = view.findViewById(R.id.txtMenuName)
        val cardView: View = itemView

    }


}