package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.MerchantProductClickListener
import com.plasdor.app.model.MerchantProductListItems
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.ThreeTwoImageView
import com.plasdor.app.utils.setImage

class MerchantProductListAdapter (
    activityContext: Context,
    private val merchantProductClickListener: MerchantProductClickListener

) :
    RecyclerView.Adapter<MerchantProductListAdapter.MyViewHolder>() {

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

        holder.lblDiscountedPrice.text = "Offer Rs." + item.priceToSell
        holder.lblPrice.text = "Rs." + item.price
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

        holder.btnWishList.setImageResource(R.drawable.ic_baseline_remove_circle_24)

        holder.btnWishList.setOnClickListener {
            //remove from list
            merchantProductClickListener.removeMyProduct(item, position)
        }

        holder.soldLayout.setOnClickListener {
            merchantProductClickListener.addToAvailable(item, position)
        }

        holder.itemView.setOnClickListener {
            merchantProductClickListener.selectProduct(item, position)
        }
        holder.btnEdit.setOnClickListener {
            merchantProductClickListener.editProduct(item, position)
        }

        holder.btnEdit.visibility=View.VISIBLE
        holder.layoutQty.visibility=View.VISIBLE
        holder.layoutQty2.visibility=View.VISIBLE
        holder.txtTotalQty.text = item.totalQty
        holder.txtSold.text = item.soldQty
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageLoader: ProgressBar? = view.findViewById(R.id.progressBar)
        var productImage: ThreeTwoImageView? = view.findViewById(R.id.productImage)
        var txtProductName: AppCompatTextView = view.findViewById(R.id.lblProductName)
        var lblType: AppCompatTextView = view.findViewById(R.id.lblType)
        var lblPrice: AppCompatTextView = view.findViewById(R.id.lblPrice)
        var lblDiscountedPrice: AppCompatTextView = view.findViewById(R.id.lblDiscountedPrice)
        var btnWishList: AppCompatImageView = view.findViewById(R.id.btnWishList)
        var soldLayout: RelativeLayout = view.findViewById(R.id.soldLayout)
        var btnDelete: AppCompatImageView = view.findViewById(R.id.btnDelete)
        var btnEdit: AppCompatImageView = view.findViewById(R.id.btnEdit)
        var adminBtnLayout: LinearLayout = view.findViewById(R.id.adminBtnLayout)
        var layoutQty: LinearLayout = view.findViewById(R.id.layoutQty)
        var layoutQty2: LinearLayout = view.findViewById(R.id.layoutQty2)
        var txtTotalQty: AppCompatTextView = view.findViewById(R.id.txtTotalQty)
        var txtSold: AppCompatTextView = view.findViewById(R.id.txtSold)
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