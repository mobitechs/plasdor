package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.MerchantProductClickListener
import com.plasdor.app.model.MerchantProductListItems
import com.plasdor.app.utils.ThreeTwoImageView
import com.plasdor.app.utils.setImage

class MerchantAllProductListAdapter(
    activityContext: Context,
    private val merchantProductClickListener: MerchantProductClickListener
) :
    RecyclerView.Adapter<MerchantAllProductListAdapter.MyViewHolder>() {

    private var allListItems = ArrayList<MerchantProductListItems>()
    private var listItems = ArrayList<MerchantProductListItems>()

    var context: Context = activityContext

    lateinit var adapter: ArrayAdapter<String>

    fun updateListItems(items: ArrayList<MerchantProductListItems>) {
        listItems.clear()
        listItems.addAll(items)
        allListItems.clear()
        allListItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_list_items_product, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: MerchantProductListItems = listItems.get(position)

        holder.txtProductName.text = item.productName
        holder.lblType.text = "Type: " + item.type

        holder.lblDiscountedPrice.text = "Offer Rs." + item.priceToSellDaily
        holder.lblPrice.text = "Rs." + item.priceToShowDaily
        holder.lblPrice.setPaintFlags(holder.lblPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        val imagepath = item.img
        if (imagepath == null || imagepath == " ") {
            holder.productImage!!.background =
                context.resources.getDrawable(R.drawable.img_not_available)
            holder.imageLoader!!.visibility = View.GONE
        } else {
            holder.productImage!!.setImage(imagepath)
            holder.imageLoader!!.visibility = View.GONE
        }

        if(item.isSold.equals("1")){
            holder.soldLayout.visibility = View.VISIBLE
        }else{
            holder.soldLayout.visibility = View.GONE
        }

        if (item.isAdded == "1") {
            holder.btnWishList.setImageResource(R.drawable.ic_baseline_remove_circle_24)
        } else {
            holder.btnWishList.setImageResource(R.drawable.ic_outline_add_circle_24)
        }

        holder.btnWishList.setOnClickListener {
            if (item.isAdded == "1") {
//                holder.btnWishList.setImageResource(R.drawable.ic_outline_add_circle_24)
                merchantProductClickListener.removeMyProduct(item, position)
            } else {
//                holder.btnWishList.setImageResource(R.drawable.ic_baseline_remove_circle_24)
                merchantProductClickListener.addMyProduct(item, position)
            }
        }

        holder.soldLayout.setOnClickListener {
            merchantProductClickListener.addToAvailable(item, position)
        }

        holder.itemView.setOnClickListener {
            merchantProductClickListener.selectProduct(item, position)
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageLoader: ProgressBar? = view.findViewById(R.id.progressBar)
        var productImage: ThreeTwoImageView? = view.findViewById(R.id.productImage)
        var txtProductName: TextView = view.findViewById(R.id.lblProductName)
        var lblType: TextView = view.findViewById(R.id.lblType)
        var lblPrice: TextView = view.findViewById(R.id.lblPrice)
        var lblDiscountedPrice: TextView = view.findViewById(R.id.lblDiscountedPrice)
        var btnWishList: AppCompatImageView = view.findViewById(R.id.btnWishList)
        var soldLayout: RelativeLayout = view.findViewById(R.id.soldLayout)
        var btnDelete: AppCompatImageView = view.findViewById(R.id.btnDelete)
        var btnEdit: AppCompatImageView = view.findViewById(R.id.btnEdit)
        var adminBtnLayout: LinearLayout = view.findViewById(R.id.adminBtnLayout)
        val cardView: View = itemView

    }


    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    listItems = allListItems
                } else {
                    val filteredList: MutableList<MerchantProductListItems> = ArrayList()
                    for (row in allListItems) {
                        if (row.productName.toLowerCase()
                                .contains(charString.toLowerCase()) || row.type.toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    listItems = filteredList as ArrayList<MerchantProductListItems>
                }
                val filterResults = FilterResults()
                filterResults.values = listItems
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                listItems = filterResults.values as ArrayList<MerchantProductListItems>
                notifyDataSetChanged()
            }
        }
    }
}